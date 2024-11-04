package dev.tekofx.biblioteques.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import dev.tekofx.biblioteques.ui.screens.book.BookScreen
import dev.tekofx.biblioteques.ui.screens.book.BooksScreen
import dev.tekofx.biblioteques.ui.screens.library.LibrariesScreen
import dev.tekofx.biblioteques.ui.screens.library.LibraryScreen
import dev.tekofx.biblioteques.ui.viewModels.BookViewModel
import dev.tekofx.biblioteques.ui.viewModels.library.LibraryViewModel


@Composable
fun Navigation(
    navController: NavHostController,
    libraryViewModel: LibraryViewModel,
    bookViewModel: BookViewModel
) {


    NavHost(navController = navController, startDestination = NavScreen.LibrariesScreen.name) {
        composable(NavScreen.LibrariesScreen.name) {
            LibrariesScreen(navController, libraryViewModel)
        }

        composable(
            NavScreen.LibrariesScreen.name + "/{libraryId}",
            arguments = listOf(navArgument("libraryId") { type = NavType.StringType })
        ) { backStackEntry ->
            val libraryId = backStackEntry.arguments!!.getString("libraryId")!!
            LibraryScreen(libraryId, libraryViewModel)
        }

        composable(NavScreen.BooksScreen.name) {
            BooksScreen(navController, bookViewModel)
        }

        composable(
            NavScreen.BooksScreen.name + "/{libraryUrl}",
            arguments = listOf(navArgument("libraryUrl") { type = NavType.StringType })
        ) { backStackEntry ->
            val libraryUrl = backStackEntry.arguments!!.getString("libraryUrl")!!
            BookScreen(libraryUrl, bookViewModel)
        }

    }
}