package dev.tekofx.biblioteques.ui.screens.book

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import dev.tekofx.biblioteques.call.BookService
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
    }

    currentBook?.let { book ->
        Column {
            AsyncImage(
                model = book.image, // Ajusta con tu imagen
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2F)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )
            Text(text = book.title, style = Typography.titleLarge)
            Text(text = book.author, style = Typography.titleMedium)
            Text(text = book.publication, style = Typography.titleMedium)
        }

    }


}