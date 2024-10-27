package dev.tekofx.biblioteques.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import dev.tekofx.biblioteques.call.LibraryService
import dev.tekofx.biblioteques.model.Library
import dev.tekofx.biblioteques.repository.LibraryRepository
import dev.tekofx.biblioteques.ui.home.HomeViewModel

@Composable
fun HomeScreen() {
    val libraryRepository = LibraryRepository(LibraryService.getInstance())
    val homeViewModel = HomeViewModel(libraryRepository)
    Column(Modifier.fillMaxSize()) {

        Text(
            text = "HomeScreen",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "test2",
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = "test3",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.weight(1f))
        LibraryList(libraries = homeViewModel.libraries)
        DisposableEffect(Unit) {
            homeViewModel.getLibraries()
            onDispose {}
        }
        Button(onClick = { /* Handle click */ }, Modifier.fillMaxWidth()) {
            Text(text = "Button")
        }
    }
}


@Composable
fun ListItem(data: Library?, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth()) {
        if (data != null) {
            Text(text = data.adrecaNom)
        }
    }
}

@Composable
fun LibraryList(libraries: SnapshotStateList<Library>) {
    LazyColumn {
        items(libraries) { dog ->
            ListItem(dog)
        }
    }
}