package dev.tekofx.bibliotecat.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
import dev.tekofx.bibliotecat.R
import dev.tekofx.bibliotecat.ui.IconResource

sealed class BottomNavigationItems(
    val title: String,
    val selectedIcon: IconResource,
    val unselectedIcon: IconResource,
    val route: String
) {
    data object Libraries : BottomNavigationItems(
        title = "Biblioteques",
        selectedIcon = IconResource.fromImageVector(Icons.Filled.Home),
        unselectedIcon = IconResource.fromImageVector(Icons.Outlined.Home),
        route = NavigateDestinations.LIBRARIES_ROUTE
    )

    data object Catalog : BottomNavigationItems(
        title = "Catàleg",
        selectedIcon = IconResource.fromDrawableResource(R.drawable.menu_book_filled),
        unselectedIcon = IconResource.fromDrawableResource(R.drawable.menu_book),
        route = NavigateDestinations.BOOK_SEARCH_ROUTE
    )
}