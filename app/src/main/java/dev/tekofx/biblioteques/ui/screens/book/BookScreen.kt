package dev.tekofx.biblioteques.ui.screens.book

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.tekofx.biblioteques.call.BookService
import dev.tekofx.biblioteques.repository.BookRepository
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

    val book by bookViewModel.currentBook.observeAsState()


    // Get book info
    LaunchedEffect(book) {
        bookViewModel.filterBook(libraryUrl)
    }



    Column {
        book?.let { Text(text = it.title) }
    }

}