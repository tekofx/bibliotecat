package dev.tekofx.biblioteques.components

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.tekofx.biblioteques.call.LibraryService
import dev.tekofx.biblioteques.repository.LibraryRepository
import dev.tekofx.biblioteques.ui.home.HomeViewModel
import dev.tekofx.biblioteques.ui.home.HomeViewModelFactory

@Composable
fun LibraryList(
    homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(
            LibraryRepository(LibraryService.getInstance())
        )
    )
) {
    val libraries by homeViewModel.libraries.observeAsState(emptyList())
    val listState = rememberLazyListState()
    val isLoading by homeViewModel.isLoading.observeAsState(false)

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {


        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {

                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp)
                ) {
                    items(libraries) { library ->
                        LibraryItem(library)
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                        )
                    }
                }
            }
        }
        OutlinedTextField(
            value = homeViewModel.queryText,
            onValueChange = { newText ->
                homeViewModel.onSearchTextChanged(newText)
            },
            singleLine = true,
            leadingIcon = { Icon(imageVector = Icons.Outlined.Search, contentDescription = null) },
            shape = RoundedCornerShape(50.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),

            label = { Text("Buscar") }
        )

    }


}

@Preview
@Composable
fun LibraryListPreview() {
    LibraryList()
}