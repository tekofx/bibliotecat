package dev.tekofx.biblioteques.ui.screens.book

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import dev.tekofx.biblioteques.R
import dev.tekofx.biblioteques.model.StatusColor
import dev.tekofx.biblioteques.model.book.BookCopy
import dev.tekofx.biblioteques.model.book.BookDetails
import dev.tekofx.biblioteques.navigation.NavigateDestinations
import dev.tekofx.biblioteques.ui.IconResource
import dev.tekofx.biblioteques.ui.components.InfoCard
import dev.tekofx.biblioteques.ui.components.StatusBadge
import dev.tekofx.biblioteques.ui.components.animations.SlideDirection
import dev.tekofx.biblioteques.ui.components.animations.SlideVertically
import dev.tekofx.biblioteques.ui.components.input.SearchBar
import dev.tekofx.biblioteques.ui.theme.Typography
import dev.tekofx.biblioteques.ui.viewModels.book.BookViewModel
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BookScreen(
    bookUrl: String,
    navController: NavHostController,
    bookViewModel: BookViewModel
) {

    val currentBook by bookViewModel.currentBook.observeAsState(null)
    val isLoadingBookCopies by bookViewModel.isLoadingBookCopies.observeAsState(false)
    val isLoadingBookDetails by bookViewModel.isLoadingBookDetails.observeAsState(false)
    val bookCopies by bookViewModel.bookCopies.observeAsState(emptyList())
    val listState = rememberLazyListState()
    val context = LocalContext.current

    // Get book info
    LaunchedEffect(key1 = null) {
        Log.d("BookScreen", "currentBook: $currentBook bookUrl:$bookUrl")

        if (currentBook == null) {
            bookViewModel.getBookDetails(bookUrl.toInt())
            Log.d("BookScreen", "Retrived book")
        }
    }


    Scaffold(
        floatingActionButton = {
            if (currentBook != null) {
                ExtendedFloatingActionButton(
                    onClick = {
                        val intent =
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://aladi.diba.cat/${currentBook!!.url}")
                            )
                        context.startActivity(intent)
                    },
                    text = { Text("Veure en Aladi") },
                    icon = {
                        Icon(
                            IconResource.fromDrawableResource(R.drawable.public_icon)
                                .asPainterResource(),
                            contentDescription = "web"
                        )
                    }

                )
            }
        }
    ) {
        if (currentBook == null) {
            Text(text = "No es puc trobar el llibre", textAlign = TextAlign.Justify)
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    AsyncImage(
                        model = currentBook!!.image,
                        contentDescription = null,
                        placeholder = rememberVectorPainter(image = Icons.Outlined.AccountBox),
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(2F)
                            .clip(RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        tonalElevation = 20.dp,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = currentBook!!.title,
                                style = Typography.titleLarge,
                            )
                            Text(text = currentBook!!.author, style = Typography.titleMedium)
                            currentBook!!.publication?.let {
                                HorizontalDivider(thickness = 2.dp)
                                Text(text = "Publicació")
                                Text(text = it, style = Typography.titleMedium)
                            }
                        }
                    }
                }
                item {
                    BookDetailsSegment(currentBook!!.bookDetails, isLoadingBookDetails)
                }
                item {
                    BookCopiesSegment(
                        noBookCopies = currentBook!!.bookCopies.isEmpty(),
                        bookCopies = bookCopies,
                        showLoading = isLoadingBookCopies,
                        show = !(isLoadingBookCopies || isLoadingBookDetails),
                        onBookCopyClick = {
                            navController.navigate(NavigateDestinations.LIBRARY_DETAILS_ROUTE + "?libraryUrl=${it}")
                        },
                        showAvailableNow = bookViewModel.availableNowChip,
                        showCanReserve = bookViewModel.canReserveChip,
                        onAvailableNowChipClick = { bookViewModel.onAvailableNowChipClick() },
                        onCanReserveChipClick = { bookViewModel.onCanReserveChipClick() },
                        onTextFieldChange = { bookViewModel.onTextFieldValueChange(it) },
                        textFieldValue = bookViewModel.bookCopiesTextFieldValue,
                        listState = listState
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(400.dp))
                }
            }
        }
    }
}


@Composable
fun BookDetailsSegment(
    bookDetails: BookDetails?,
    isLoading: Boolean
) {
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        }
    }
    SlideVertically(
        visible = bookDetails != null,
        SlideDirection.UP
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (bookDetails != null) {
                bookDetails.edition?.let {
                    InfoCard("Edició", it)
                }
                bookDetails.description?.let {
                    InfoCard("Descripció", it)
                }
                bookDetails.collection?.let {
                    InfoCard("Col·lecció", it)
                }
                bookDetails.isbn?.let {
                    InfoCard("ISBN", it)
                }
                bookDetails.synopsis?.let {
                    InfoCard("Sinopsi", it)
                }
                bookDetails.topic?.let {
                    InfoCard("Tema", it)
                }
            }
        }
    }
}


@Composable
fun BookCopiesSegment(
    noBookCopies: Boolean,
    bookCopies: List<BookCopy>,
    showLoading: Boolean,
    show: Boolean,
    showAvailableNow: Boolean,
    showCanReserve: Boolean,
    onCanReserveChipClick: () -> Unit,
    onAvailableNowChipClick: () -> Unit,
    onBookCopyClick: (libraryUrl: String) -> Unit,
    onTextFieldChange: (String) -> Unit,
    textFieldValue: String,
    listState: LazyListState
) {
    if (showLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        }
    }
    SlideVertically(
        visible = show,
        SlideDirection.UP
    ) {

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Exemplars",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = Typography.titleLarge,
            )
            if (noBookCopies) {

                Text("No hi ha exemplars")
            } else {

                BookCopiesFilters(
                    showAvailableNow = showAvailableNow,
                    showCanReserve = showCanReserve,
                    onAvailableNowChipClick = onAvailableNowChipClick,
                    onCanReserveChipClick = onCanReserveChipClick,
                    onTextFieldChange = onTextFieldChange,
                    textFieldValue = textFieldValue,
                    listState = listState
                )

                bookCopies.forEach { bookCopy: BookCopy ->
                    BookCopyCard(
                        bookCopy,
                        onBookCopyClick
                    )
                }
                if (bookCopies.isEmpty()) {
                    Text(text = "No hi ha exemplars amb aquests filtres")
                }
            }
        }
    }


}

@Composable
fun BookCopiesFilters(
    showAvailableNow: Boolean,
    onAvailableNowChipClick: () -> Unit,
    onCanReserveChipClick: () -> Unit,
    showCanReserve: Boolean,
    onTextFieldChange: (String) -> Unit,
    textFieldValue: String,
    listState: LazyListState
) {
    val focusRequester = remember { FocusRequester() }
    val coroutineScope = rememberCoroutineScope()
    var textFieldY by remember { mutableFloatStateOf(0f) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SearchBar(
            value = textFieldValue,
            label = "Filtra",
            onDone = {},
            onValueChange = onTextFieldChange,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .onGloballyPositioned { coordinates ->
                    textFieldY = coordinates.positionInRoot().y
                }
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        coroutineScope.launch {
                            listState.animateScrollToItem(3)
                        }
                    }
                },
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterChipComponent(
                text = "Disponible Ara",
                selected = showAvailableNow,
                statusColor = StatusColor.GREEN,
                onClick = { onAvailableNowChipClick() }
            )
            FilterChipComponent(
                text = "Es pot reservar",
                selected = showCanReserve,
                statusColor = StatusColor.YELLOW,
                onClick = { onCanReserveChipClick() }
            )
        }
    }
}


@Composable
fun BookCopyCard(
    bookCopy: BookCopy,
    onClick: (String) -> Unit,
) {
    Surface(
        tonalElevation = 20.dp,
        shape = RoundedCornerShape(10.dp),
        onClick = {
            if (bookCopy.bibliotecaVirtualUrl != null) {
                onClick(bookCopy.bibliotecaVirtualUrl)
            }
        }
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = bookCopy.location,
                style = Typography.titleMedium
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(text = "Signatura")
                Text(text = bookCopy.signature)

            }
            bookCopy.notes?.let {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(text = "Notes")
                    Text(text = it)
                }
            }
            StatusBadge(
                bookCopy.statusColor,
                text = bookCopy.status,
                textStyle = Typography.bodyMedium
            )

        }
    }
}


@Composable
fun FilterChipComponent(
    text: String,
    selected: Boolean,
    statusColor: StatusColor,
    onClick: () -> Unit
) {
    FilterChip(
        onClick = { onClick() },
        label = {
            StatusBadge(
                text = text,
                statusColor = statusColor
            )
        },
        selected = selected,
        leadingIcon = if (selected) {
            {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = "Done icon",
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                )
            }
        } else {
            null
        },
    )
}