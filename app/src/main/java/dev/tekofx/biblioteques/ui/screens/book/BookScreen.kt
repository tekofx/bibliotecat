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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.tekofx.biblioteques.model.book.BookCopy
import dev.tekofx.biblioteques.model.book.BookCopyAvailability
import dev.tekofx.biblioteques.model.book.BookDetails
import dev.tekofx.biblioteques.ui.components.InfoCard
import dev.tekofx.biblioteques.ui.components.animations.SlideDirection
import dev.tekofx.biblioteques.ui.components.animations.SlideVertically
import dev.tekofx.biblioteques.ui.components.book.BookCopyCard
import dev.tekofx.biblioteques.ui.theme.Typography
import dev.tekofx.biblioteques.ui.viewModels.BookViewModel

@Composable
fun BookScreen(
    bookUrl: String,
    bookViewModel: BookViewModel
) {

    val currentBook by bookViewModel.currentBook.observeAsState(null)
    val isLoading by bookViewModel.isLoading.observeAsState(false)

    // Get book info
    LaunchedEffect(key1 = null) {
        if (currentBook == null || currentBook!!.id != bookUrl.toInt()) {
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
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }
            }
            BookDetailsSegment(currentBook!!.bookDetails)
            BookCopiesSegment(currentBook!!.bookCopies, isLoading)


        }
    }
}


@Composable
fun BookDetailsSegment(
    bookDetails: BookDetails?

) {
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
    bookCopies: List<BookCopy>,
    isLoading: Boolean
) {
    var showOnlyAvailable by remember { mutableStateOf(false) }
    SlideVertically(
        visible = !isLoading,
        SlideDirection.UP
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Exemplars",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = Typography.titleLarge,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(text = "Obert ara", style = Typography.bodyLarge)
                Switch(
                    checked = showOnlyAvailable,
                    onCheckedChange = {
                        showOnlyAvailable = it
                    }
                )
            }
            if (bookCopies.isEmpty()) {
                Text("No hi ha exemplars")
            } else {
                val filteredBookCopies = if (showOnlyAvailable) {
                    bookCopies.filter { it.availability == BookCopyAvailability.AVAILABLE || it.availability == BookCopyAvailability.CAN_RESERVE }
                } else {
                    bookCopies
                }

                filteredBookCopies.forEach { bookCopy: BookCopy ->
                    BookCopyCard(bookCopy)
                }
            }
        }
    }

}
