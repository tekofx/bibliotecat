package dev.tekofx.biblioteques.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.tekofx.biblioteques.components.LibraryList

@Composable
fun HomeScreen() {
    Column(Modifier.fillMaxSize()) {
        LibraryList()
    }
}

