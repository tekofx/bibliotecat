package dev.tekofx.biblioteques.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import dev.tekofx.biblioteques.components.LibraryList

@Composable
fun LibrariesScreen(navHostController: NavHostController) {
    Column(Modifier.fillMaxSize()) {
        LibraryList(navHostController)
    }
}

