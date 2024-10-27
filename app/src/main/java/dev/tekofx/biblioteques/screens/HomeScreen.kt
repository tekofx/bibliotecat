package dev.tekofx.biblioteques.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.tekofx.biblioteques.call.LibraryService
import dev.tekofx.biblioteques.components.LibraryList
import dev.tekofx.biblioteques.repository.LibraryRepository
import dev.tekofx.biblioteques.ui.home.HomeViewModel

@Composable
fun HomeScreen() {
    val libraryRepository = LibraryRepository(LibraryService.getInstance())
    val homeViewModel = HomeViewModel(libraryRepository)
    Column(Modifier.fillMaxSize()) {
        LibraryList(homeViewModel)
        DisposableEffect(Unit) {
            homeViewModel.getLibraries()
            onDispose {}
        }
        Button(onClick = { /* Handle click */ }, Modifier.fillMaxWidth()) {
            Text(text = "Button")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewLibraryListScreen() {
    val viewModel = HomeViewModel(LibraryRepository(LibraryService.getInstance()))
    LibraryList(viewModel = viewModel)
}