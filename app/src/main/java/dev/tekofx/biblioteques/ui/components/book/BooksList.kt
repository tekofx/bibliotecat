package dev.tekofx.biblioteques.ui.components.book

import android.util.Log
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import dev.tekofx.biblioteques.call.BookService
import dev.tekofx.biblioteques.model.book.Book
import dev.tekofx.biblioteques.repository.BookRepository
import dev.tekofx.biblioteques.ui.viewModels.BookViewModel
import dev.tekofx.biblioteques.ui.viewModels.BookViewModelFactory
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

@Composable
fun BooksList(
    books: List<Book>,
    navHostController: NavHostController,
    bookViewModel: BookViewModel = viewModel(
        factory = BookViewModelFactory(
            BookRepository(BookService.getInstance())
        )
    )
) {
    val density = LocalDensity.current
    val listState = rememberLazyListState()
    val isLoading by bookViewModel.isLoading.observeAsState(false)
    val shouldLoadMore = remember {
        derivedStateOf {
            val totalItemsCount = listState.layoutInfo.totalItemsCount
            val lastVisibleItemIndex =
                listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleItemIndex >= (totalItemsCount - 2) && !isLoading && books.isNotEmpty()
        }
    }

    LaunchedEffect(listState) {
        Log.d("BooksList", "liststate")
        snapshotFlow { shouldLoadMore.value }
            .distinctUntilChanged()
            .filter { it }  // Ensure that we load more items only when needed
            .collect {
                println("loadMore")
                bookViewModel.getResultPage()
            }

    }


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


        }
    }
}