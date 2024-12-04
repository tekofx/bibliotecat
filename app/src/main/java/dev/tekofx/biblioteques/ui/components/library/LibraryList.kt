package dev.tekofx.biblioteques.ui.components.library

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.tekofx.biblioteques.model.library.Library
import dev.tekofx.biblioteques.ui.components.animations.SlideDirection
import dev.tekofx.biblioteques.ui.components.animations.SlideVertically

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LibraryList(
    onLibraryCardClick: (libraryId: String) -> Unit,
    libraries: List<Library>,
    isLoading: Boolean
) {
    val listState = rememberLazyListState()

    LaunchedEffect(libraries) {
        listState.scrollToItem(0)
    }

    SlideVertically(
        visible = !isLoading,
        SlideDirection.UP,
    ) {
        if (libraries.isEmpty()) {
            Text("No hi ha biblioteques amb aquestes filtres")
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(libraries, key = { it.id }) { library ->
                Row(modifier = Modifier.animateItem()) {
                    LibraryCard(
                        library = library,
                        onClick = {
                            onLibraryCardClick(library.id)
                        }
                    )
                }
            }

        }
    }
}

