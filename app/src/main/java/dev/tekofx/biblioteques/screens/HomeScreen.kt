package dev.tekofx.biblioteques.screens

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.tekofx.biblioteques.components.LibraryList
import dev.tekofx.biblioteques.ui.home.HomeViewModel

@Composable
fun HomeScreen(homeViewModel: HomeViewModel) {
    Column(Modifier.fillMaxSize()) {
        Box(Modifier.weight(1f)) {

            LibraryList(homeViewModel)
//            DisposableEffect(Unit) {
//                homeViewModel.getLibraries()
//                onDispose {}
//            }
        }
        TextField(
            value = homeViewModel.queryText,
            onValueChange = { newText ->
                homeViewModel.onSearchTextChanged(newText)
            },

            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            label = { Text("Buscar") }
        )
    }
    LaunchedEffect(Unit) {

        Log.d("HomeScreen", "LaunchedEffect called")
        homeViewModel.getLibraries()
    }
}

