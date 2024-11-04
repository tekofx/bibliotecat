package dev.tekofx.biblioteques.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.tekofx.biblioteques.call.BookService
import dev.tekofx.biblioteques.repository.BookRepository
import dev.tekofx.biblioteques.ui.viewModels.BookViewModel
import dev.tekofx.biblioteques.ui.viewModels.BookViewModelFactory

@Composable
fun BooksScreen(
    bookViewModel: BookViewModel = viewModel(
        factory = BookViewModelFactory(
            BookRepository(BookService.getInstance())
        )
    )
) {

    val listState = rememberLazyListState()
    val books by bookViewModel.books.observeAsState(emptyList())
    var query by remember { mutableStateOf("") }


    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Column(
            modifier = Modifier
                .padding(horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            TextField(
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                value = query,
                onValueChange = { newText ->
                    query = newText
                },
                singleLine = true,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = null
                    )
                },
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .fillMaxWidth(),
                label = { Text("Llibre") }
            )
            Button(
                onClick = { bookViewModel.getBook(query) }
            ) {
                Text(text = "Cerca")
            }

            if (books.isNotEmpty()) {

                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 10.dp)
                ) {
                    items(books) { book ->
                        Text(text = book.title)
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                        )
                    }
                }
            }

        }
    }
}


@Preview
@Composable
fun BooksScreenPreview() {
    BooksScreen()
}