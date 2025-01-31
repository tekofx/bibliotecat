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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.tekofx.biblioteques.model.library.Library
import dev.tekofx.biblioteques.ui.components.animations.SlideDirection
import dev.tekofx.biblioteques.ui.components.animations.SlideVertically
import dev.tekofx.biblioteques.ui.theme.Typography

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LibraryList(
    onLibraryCardClick: (libraryId: String) -> Unit,
    filtersApplied: Boolean,
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
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp,Alignment.CenterVertically)
        ) {
            item{
                if (libraries.isEmpty() && filtersApplied) {
                    Text(
                        text = "No hi ha biblioteques amb aquestes filtres",
                        modifier = Modifier.padding(16.dp),
                        style = Typography.headlineLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }
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

