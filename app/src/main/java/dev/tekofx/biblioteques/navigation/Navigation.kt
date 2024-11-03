package dev.tekofx.biblioteques.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import dev.tekofx.biblioteques.ui.screens.DashboardScreen
import dev.tekofx.biblioteques.ui.screens.LibrariesScreen
import dev.tekofx.biblioteques.ui.screens.library.LibraryScreen

@Composable
fun Navigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = NavScreen.LibrariesScreen.name) {
        composable(NavScreen.LibrariesScreen.name) {
            LibrariesScreen(navController)
        }

        composable(NavScreen.BooksScreen.name) {
            DashboardScreen()
        }

        composable(
            NavScreen.LibraryScreen.name + "/{libraryId}",
            arguments = listOf(navArgument("libraryId") { type = NavType.StringType })
        ) { backStackEntry ->
            val libraryId = backStackEntry.arguments!!.getString("libraryId")!!
            LibraryScreen(libraryId)
        }

    }
}