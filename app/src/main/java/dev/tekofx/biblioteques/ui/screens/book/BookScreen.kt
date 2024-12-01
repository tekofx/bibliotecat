package dev.tekofx.biblioteques.ui.screens.book

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import dev.tekofx.biblioteques.model.StatusColor
import dev.tekofx.biblioteques.model.book.BookCopy
import dev.tekofx.biblioteques.model.book.BookDetails
import dev.tekofx.biblioteques.navigation.NavigateDestinations
import dev.tekofx.biblioteques.ui.components.InfoCard
import dev.tekofx.biblioteques.ui.components.StatusBadge
import dev.tekofx.biblioteques.ui.components.animations.SlideDirection
import dev.tekofx.biblioteques.ui.components.animations.SlideVertically
import dev.tekofx.biblioteques.ui.theme.Typography
import dev.tekofx.biblioteques.ui.viewModels.book.BookViewModel

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


    // Get book info
    LaunchedEffect(key1 = null) {
        Log.d("BookScreen", "currentBook: $currentBook bookUrl:$bookUrl")

        if (currentBook == null) {
            bookViewModel.getBookDetails(bookUrl.toInt())
            Log.d("BookScreen", "Retrived book")
        }
    }


    if (currentBook == null) {
        Text(text = "No es puc trobar el llibre", textAlign = TextAlign.Justify)
    } else {
        Column(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
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
            BookDetailsSegment(currentBook!!.bookDetails, isLoadingBookDetails)
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
                onCanReserveChipClick = { bookViewModel.onCanReserveChipClick() }
            )
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