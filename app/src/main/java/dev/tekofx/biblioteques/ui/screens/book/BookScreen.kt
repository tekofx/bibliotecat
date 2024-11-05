package dev.tekofx.biblioteques.ui.screens.book

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import dev.tekofx.biblioteques.call.BookService
import dev.tekofx.biblioteques.model.book.BookCopy
import dev.tekofx.biblioteques.repository.BookRepository
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

    // Get book info
    LaunchedEffect(key1 = null) {
        bookViewModel.filterBook(libraryUrl)
        Log.d("BookScreen", "Retrived book")
    }

    // Observe currentBook and trigger getBookCopies when it's not null
    LaunchedEffect(currentBook) {
        currentBook?.let {
            if (currentBook!!.bookCopies.isEmpty()) {

                bookViewModel.getBookCopies()
                Log.d("BookScreen", "Got bookcopies ${currentBook!!.bookCopies}")
            }
        }
    }

    if (currentBook == null) {

    } else {


        Column(
            modifier = Modifier.padding(horizontal = 10.dp)
        ) {
            AsyncImage(
                model = currentBook!!.image, // Ajusta con tu imagen
                contentDescription = null,
                placeholder = rememberVectorPainter(image = Icons.Outlined.AccountBox),
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2F)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )
            Text(text = currentBook!!.title, style = Typography.titleLarge)
            Text(text = currentBook!!.author, style = Typography.titleMedium)
            Text(text = currentBook!!.publication, style = Typography.titleMedium)

            currentBook!!.bookCopies.forEach { bookCopy: BookCopy ->
                Column {

                    Text(text = bookCopy.location)
                    Text(text = bookCopy.signature)
                }

            }

        }
    }
}

