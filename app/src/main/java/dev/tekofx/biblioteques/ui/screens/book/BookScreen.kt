package dev.tekofx.biblioteques.ui.screens.book

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import dev.tekofx.biblioteques.R
import dev.tekofx.biblioteques.model.StatusColor
import dev.tekofx.biblioteques.model.book.Book
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
import dev.tekofx.biblioteques.utils.IntentType
import dev.tekofx.biblioteques.utils.openApp
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BookScreen(
    bookUrl: String,
    navController: NavHostController,
    bookViewModel: BookViewModel
) {

    // Data
    val currentBook by bookViewModel.currentBook.collectAsState()
    val bookCopies by bookViewModel.bookCopies.collectAsState()
    val areThereMoreCopies by bookViewModel.areThereMoreCopies.collectAsState()

    // Input
    val availableNowChip by bookViewModel.availableNowChip.collectAsState()
    val canReserveChip by bookViewModel.canReserveChip.collectAsState()
    val bookCopiesTextFieldValue by bookViewModel.bookCopiesTextFieldValue.collectAsState()

    // Loaders
    val isLoadingBookCopies by bookViewModel.isLoadingBookCopies.collectAsState()
    val isLoadingMoreBookCopies by bookViewModel.isLoadingMoreBookCopies.collectAsState()
    val isLoadingBookDetails by bookViewModel.isLoadingBookDetails.collectAsState()

    val listState = rememberLazyListState()
    val context = LocalContext.current

    // FloatingActionButton visibility state
    val isFabVisible = remember { mutableStateOf(true) }


    val fullscreenCover = remember { mutableStateOf(false) }

    // Get book info
    LaunchedEffect(key1 = null) {
        Log.d("BookScreen", "currentBook: ${currentBook?.title} bookUrl:$bookUrl")

        if (currentBook == null) {
            bookViewModel.getBookDetails(bookUrl.toInt())
            Log.d("BookScreen", "Retrived book")
        }
    }


    // Observe scroll state to hide/show FAB
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
            .collect { visibleItems ->
                isFabVisible.value = visibleItems.any { it.index == 1 }
            }
    }

    Scaffold(
        floatingActionButton = {
            AnimatedVisibility(
                visible = isFabVisible.value && currentBook != null,
            ) {
                ExtendedFloatingActionButton(
                    onClick = {
                        openApp(
                            context,
                            IntentType.WEB,
                            "https://aladi.diba.cat/${currentBook!!.url}"
                        )
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
            FullScreenCover(fullscreenCover, currentBook)
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    AsyncImage(
                        model = currentBook!!.image,
                        contentDescription = null,
                        placeholder = rememberVectorPainter(image = Icons.Outlined.AccountBox),
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                fullscreenCover.value = !fullscreenCover.value
                            },
                        contentScale = ContentScale.Crop,
                    )
                }
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        tonalElevation = 20.dp,
                        shape = MaterialTheme.shapes.small
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
                        isLoadingBookCopies = isLoadingBookCopies,
                        isLoadingMoreBookCopies = isLoadingMoreBookCopies,
                        show = !(isLoadingBookCopies || isLoadingBookDetails),
                        onBookCopyClick = {
                            navController.navigate(NavigateDestinations.LIBRARY_DETAILS_ROUTE + "?libraryUrl=${it}")
                        },
                        showAvailableNow = availableNowChip,
                        showCanReserve = canReserveChip,
                        areThereMoreCopies = areThereMoreCopies,
                        getMoreBookCopies = { bookViewModel.getMoreBookCopies(currentBook!!) },
                        onAvailableNowChipClick = bookViewModel::onAvailableNowChipClick,
                        onCanReserveChipClick = bookViewModel::onCanReserveChipClick,
                        onTextFieldChange = bookViewModel::onBookCopiesTextfieldChange,
                        textFieldValue = bookCopiesTextFieldValue,
                        listState = listState
                    )
                }

            }
        }
    }
}

@Composable
private fun FullScreenCover(
    showImage: MutableState<Boolean>,
    currentBook: Book?
) {
    AnimatedVisibility(
        modifier = Modifier.zIndex(100F),
        visible = showImage.value,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                tonalElevation = 10.dp,
                onClick = { showImage.value = !showImage.value }
            ) {

                AsyncImage(
                    model = currentBook!!.image,
                    contentDescription = null,
                    placeholder = rememberVectorPainter(image = Icons.Outlined.AccountBox),
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp)),
                )
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

                if (bookDetails.collections.isNotEmpty()) {
                    InfoCard(
                        "Col·leccions",
                        bookDetails.collections.joinToString(prefix = "- ", separator = "\n- ")
                    )
                }


                bookDetails.isbn?.let {
                    InfoCard("ISBN", it)
                }
                bookDetails.synopsis?.let {
                    InfoCard("Sinopsi", it)
                }
                if (bookDetails.topics.isNotEmpty()) {
                    InfoCard(
                        "Tema",
                        bookDetails.topics.joinToString(prefix = "- ", separator = "\n- ")
                    )
                }
            }
        }
    }
}


@Composable
fun BookCopiesSegment(
    noBookCopies: Boolean,
    bookCopies: List<BookCopy>,
    isLoadingBookCopies: Boolean,
    isLoadingMoreBookCopies: Boolean,
    show: Boolean,
    showAvailableNow: Boolean,
    areThereMoreCopies: Boolean,
    getMoreBookCopies: () -> Unit,
    showCanReserve: Boolean,
    onCanReserveChipClick: () -> Unit,
    onAvailableNowChipClick: () -> Unit,
    onBookCopyClick: (libraryUrl: String) -> Unit,
    onTextFieldChange: (String) -> Unit,
    textFieldValue: String,
    listState: LazyListState
) {
    if (isLoadingBookCopies) {
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
                if (areThereMoreCopies) {

                    if (isLoadingMoreBookCopies) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                        ) {
                            CircularProgressIndicator(Modifier.align(Alignment.Center))
                        }
                    } else {
                        Button(
                            onClick = { getMoreBookCopies() },
                            modifier = Modifier.padding(10.dp)
                        ) {
                            Text("Carrega més")
                        }
                    }
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
        shape = MaterialTheme.shapes.small,
        onClick = {
            if (bookCopy.bibliotecaVirtualUrl != null) {
                Log.d("BookScreen", bookCopy.bibliotecaVirtualUrl)
                onClick(bookCopy.bibliotecaVirtualUrl)
            }
        }
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (bookCopy.location.contains("bibliobús", true)) {
                    Icon(
                        IconResource.fromDrawableResource(R.drawable.directions_bus)
                            .asPainterResource(),
                        contentDescription = ""
                    )
                } else {
                    Icon(
                        IconResource.fromDrawableResource(R.drawable.location_city)
                            .asPainterResource(),
                        contentDescription = ""
                    )
                }

                Text(
                    text = bookCopy.location,
                    style = Typography.titleMedium
                )
            }
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
                bookCopy.availability.color,
                text = bookCopy.availability.text,
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