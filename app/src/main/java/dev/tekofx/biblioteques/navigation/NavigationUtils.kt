package dev.tekofx.biblioteques.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun currentRoute(navController: NavController): String? =
    navController.currentBackStackEntryAsState().value?.destination?.route


fun showBottomAppBar(currentRoute: String?): Boolean {
    if (currentRoute == null) {
        return false
    }

    return when (currentRoute) {
        NavigateDestinations.LIBRARIES_ROUTE -> true
        NavigateDestinations.BOOK_SEARCH_ROUTE -> true
        else -> false
    }

}