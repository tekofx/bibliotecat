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
        NavigateDestinations.LIBRARIES_ROUTE
    )

    object Item2 : BottomNavigationItems(
        IconResource.fromDrawableResource(R.drawable.book),
        "Llibres",
        NavigateDestinations.BOOK_SEARCH_ROUTE
    )

    object Item3 : BottomNavigationItems(
        IconResource.fromDrawableResource(R.drawable.ic_notifications_black_24dp),
        "Tutorial",
        NavigateDestinations.TUTORIAL_SCREEN
    )


}