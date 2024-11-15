package dev.tekofx.biblioteques.ui.components


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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import dev.tekofx.biblioteques.model.SearchResult
import dev.tekofx.biblioteques.model.SearchResults
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

@Composable
fun <T> PaginatedList(
    searchResults: SearchResults<T>,
    key: ((item: SearchResult) -> Any)? = null,
    onLoadMore: () -> Unit,
    isLoading: Boolean,
    content: @Composable (item: SearchResult) -> Unit,
) {
    val density = LocalDensity.current
    val listState = rememberLazyListState()

    val shouldLoadMore = remember {
        derivedStateOf {
            // Get the index of the last visible item
            val lastVisibleItemIndex =
                listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

            println(lastVisibleItemIndex)
            println(listState.layoutInfo.totalItemsCount)

            // Check if we have scrolled near the end of the list and more items should be loaded
            lastVisibleItemIndex == listState.layoutInfo.totalItemsCount - 1 && searchResults.areMorePages() && listState.layoutInfo.totalItemsCount != 0 && lastVisibleItemIndex != 0 && (lastVisibleItemIndex + 1) % 12 == 0
        }
    }

    // Launch a coroutine to load more items when shouldLoadMore becomes true
    LaunchedEffect(listState) {
        snapshotFlow { shouldLoadMore.value }
            .distinctUntilChanged()
            .filter { it }  // Ensure that we load more items only when needed
            .collect {
                Log.d("PaginatedList", "Loading more results")
                onLoadMore()
            }
    }
    AnimatedVisibility(
        visible = searchResults.items.isNotEmpty(),
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
            Column {
                Surface(
                    modifier = Modifier.fillMaxWidth(),

                    tonalElevation = 20.dp,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        modifier = Modifier.padding(10.dp),
                        text = "Numero de resultats ${searchResults.numItems}"
                    )
                }
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(
                        searchResults.items,
                        key = { (it as SearchResult).id },
                    ) {
                        content(it as SearchResult)
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