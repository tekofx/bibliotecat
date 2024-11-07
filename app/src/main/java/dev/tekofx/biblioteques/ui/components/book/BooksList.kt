package dev.tekofx.biblioteques.ui.components.book

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import dev.tekofx.biblioteques.model.book.Book
import dev.tekofx.biblioteques.ui.viewModels.BookViewModel

@Composable
fun BooksList(
    books: List<Book>,
    navHostController: NavHostController,
    bookViewModel: BookViewModel
) {
    val density = LocalDensity.current
    val listState = rememberLazyListState()
    val thereAreMoreBooks by bookViewModel.thereAreMoreBooks.observeAsState(false)


    AnimatedVisibility(
        visible = books.isNotEmpty(),
        enter = slideInVertically {
            // Slide in from 40 dp from the top.
            with(density) { -40.dp.roundToPx() }
        } + expandVertically(
            // Expand from the top.
            expandFrom = Alignment.Top
        ) + fadeIn(
            // Fade in with the initial alpha of 0.3f.
            initialAlpha = 0.3f
        ),
        exit = slideOutVertically() + shrinkVertically() + fadeOut()
    ) {


        LazyColumn(
            state = listState,
            modifier = Modifier
                .padding(top = 10.dp, bottom = 20.dp)

        ) {
            items(books) { book ->
                BookCard(book, navHostController)
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                )
            }
            if (thereAreMoreBooks) {

                item {
                    Button(
                        onClick = {
                            bookViewModel.getResultPage()
                        }
                    ) {
                        Text(text = "Load more")
                    }
                }
            }


        }
    }
}