package dev.tekofx.biblioteques.ui.screens.book

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import dev.tekofx.biblioteques.call.BookService
import dev.tekofx.biblioteques.model.book.BookCopy
import dev.tekofx.biblioteques.repository.BookRepository
import dev.tekofx.biblioteques.ui.components.InfoCard
import dev.tekofx.biblioteques.ui.components.book.BookCopyCard
import dev.tekofx.biblioteques.ui.theme.Typography
import dev.tekofx.biblioteques.ui.viewModels.BookViewModel
import dev.tekofx.biblioteques.ui.viewModels.BookViewModelFactory

@Composable
fun BookScreen(
    libraryUrl: String,
    bookViewModel: BookViewModel = viewModel(
        factory = BookViewModelFactory(
            BookRepository(BookService.getInstance())
        ),
    )
) {

    val currentBook by bookViewModel.currentBook.observeAsState(null)
    val currentBookResult by bookViewModel.currentBookResult.observeAsState(null)
    val isLoading by bookViewModel.isLoading.observeAsState(false)

    // Get book info
    LaunchedEffect(key1 = null) {
        bookViewModel.filterBook(libraryUrl.toInt())
        Log.d("BookScreen", "Retrived book")
    }

    // Observe currentBook and trigger getBookCopies when it's not null
    LaunchedEffect(currentBookResult) {
        if (currentBookResult != null) {
            bookViewModel.getBookDetails()
        }
    }

    if (currentBook == null) {
        Text(text = "No es puc trobar el llibre", textAlign = TextAlign.Justify)

    } else {
        Column(
            modifier = Modifier
                .padding(horizontal = 10.dp)
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


                    Text(text = currentBook!!.title, style = Typography.titleLarge)
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
            } else {

                if (currentBook!!.bookDetails != null) {

                    currentBook!!.bookDetails!!.edition?.let {
                        InfoCard("Edició", it)
                    }
                    currentBook!!.bookDetails!!.description?.let {
                        InfoCard("Descripció", it)
                    }
                    currentBook!!.bookDetails!!.isbn?.let {
                        InfoCard("ISBN", it)
                    }
                    currentBook!!.bookDetails!!.synopsis?.let {
                        InfoCard("Sinopsi", it)
                    }
                }

                if (currentBook!!.bookCopies.isNotEmpty()) {
                    Text(text = "Exemplars", style = Typography.titleMedium)
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        currentBook!!.bookCopies.forEach { bookCopy: BookCopy ->
                            Log.d("BookScreen", "Processing book copy: $bookCopy")
                            BookCopyCard(bookCopy)
                        }
                    }
                } else {
                    Text(text = "No hi ha exemplars")
                }
            }

        }
    }
}

