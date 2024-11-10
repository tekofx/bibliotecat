package dev.tekofx.biblioteques.ui.components.book

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.navigation.NavHostController
import dev.tekofx.biblioteques.model.BookResult
import dev.tekofx.biblioteques.model.SearchResult
import dev.tekofx.biblioteques.ui.viewModels.BookViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

@Composable
fun BooksList(
    books: List<BookResult>,
    searchResults: List<SearchResult>,
    navHostController: NavHostController,
    bookViewModel: BookViewModel
) {
    val density = LocalDensity.current
    val listState = rememberLazyListState()
    val isLoading by bookViewModel.isLoading.observeAsState(false)

    val shouldLoadMore = remember {
        derivedStateOf {
            // Get the index of the last visible item
            val lastVisibleItemIndex =
                listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

            // Check if we have scrolled near the end of the list and more items should be loaded
            lastVisibleItemIndex != 0 && (lastVisibleItemIndex + 1) % 12 == 0
        }
    }

    // Launch a coroutine to load more items when shouldLoadMore becomes true
    LaunchedEffect(listState) {
        snapshotFlow { shouldLoadMore.value }
            .distinctUntilChanged()
            .filter { it }  // Ensure that we load more items only when needed
            .collect {
                Log.d("BooksList", "loading more books")
                bookViewModel.getNextResultsPage()
            }
    }
    AnimatedVisibility(
        visible = books.isNotEmpty() || searchResults.isNotEmpty(),
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
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                items(books, key = { it.id }) {
                    BookCard(it, navHostController)
                }
                itemsIndexed(
                    searchResults,
                    key = { _: Int, searchResult: SearchResult -> searchResult.text }) { _: Int, searchResult: SearchResult ->
                    Surface(
                        onClick = {
                            println(searchResult.url)
                            bookViewModel.getBooksBySearchResult(searchResult.url)
                        }
                    )
                    {
                        Row {
                            Text(
                                text = searchResult.text
                            )
                            Text(
                                text = searchResult.numEntries.toString()
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = isLoading,
                enter = slideInVertically(initialOffsetY = { it / 4 }) + expandVertically(
                    // Expand from the top.
                    expandFrom = Alignment.Bottom
                ) + fadeIn(
                    // Fade in with the initial alpha of 0.3f.
                    initialAlpha = 0.3f
                ),
                exit = slideOutVertically(targetOffsetY = { it / 4 }) + shrinkVertically() + fadeOut(),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Surface(
                    shape = RoundedCornerShape(50.dp),
                    tonalElevation = 1.dp
                ) {

                    CircularProgressIndicator()
                }
            }
        }

    }
}