package dev.tekofx.biblioteques.ui.components.library

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.tekofx.biblioteques.model.library.Library
import dev.tekofx.biblioteques.ui.components.animations.SlideDirection
import dev.tekofx.biblioteques.ui.components.animations.SlideVertically

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LibraryList(
    onLibraryCardClick: (libraryId: String) -> Unit,
    libraries: List<Library>,
    isLoading: Boolean,
    isRefreshing: Boolean,
    onRefresh: () -> Unit
) {
    val listState = rememberLazyListState()

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
    ) {
        SlideVertically(
            visible = !isLoading && libraries.isNotEmpty(),
            SlideDirection.UP,
        ) {


            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(libraries, key = { it.id }) { library ->
                    Row(modifier = Modifier.animateItem()) {
                        LibraryCard(library = library, onClick = {
                            onLibraryCardClick(library.id)
                        })
                    }
                }
            }
        }
    }
}

