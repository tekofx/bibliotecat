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
    object Libraries : BottomNavigationItems(
        IconResource.fromImageVector(Icons.Filled.Home),
        "Biblioteques",
        NavigateDestinations.LIBRARIES_ROUTE
    )

    object Catalog : BottomNavigationItems(
        IconResource.fromDrawableResource(R.drawable.book),
        "Catàleg",
        NavigateDestinations.BOOK_SEARCH_ROUTE
    )

    object Tutorial : BottomNavigationItems(
        IconResource.fromDrawableResource(R.drawable.ic_notifications_black_24dp),
        "Tutorial",
        NavigateDestinations.WELCOME_SCREEN
    )


}