package dev.tekofx.biblioteques.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import dev.tekofx.biblioteques.R
import dev.tekofx.biblioteques.ui.IconResource

sealed class BottomNavigationItems(
    val icon: IconResource,
    val title: String,
    val path: String
) {
    object Item1 : BottomNavigationItems(
        IconResource.fromImageVector(Icons.Filled.Home),
        "Biblioteques",
        NavScreen.LibrariesScreen.name
    )

    object Item2 : BottomNavigationItems(
        IconResource.fromDrawableResource(R.drawable.book),
        "Llibres",
        NavScreen.BooksScreen.name
    )


}