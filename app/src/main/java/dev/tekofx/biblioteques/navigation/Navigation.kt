package dev.tekofx.biblioteques.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import dev.tekofx.biblioteques.ui.screens.book.BookResultsScreen
import dev.tekofx.biblioteques.ui.screens.book.BookScreen
import dev.tekofx.biblioteques.ui.screens.book.BookSearchScreen
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


    NavHost(
        navController = navController,
        startDestination = NavigateDestinations.LIBRARIES_ROUTE
    ) {

        // Libraries
        composable(
            route = NavigateDestinations.LIBRARIES_ROUTE,
        ) {
            LibrariesScreen(navController, libraryViewModel)
        }

        composable(
            route = "${NavigateDestinations.LIBRARY_DETAILS_ROUTE}/{libraryId}",
            arguments = listOf(navArgument("libraryId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val libraryId = backStackEntry.arguments!!.getString("libraryId")!!
            LibraryScreen(libraryId, libraryViewModel)
        }

        // Books
        composable(
            route = NavigateDestinations.BOOK_SEARCH_ROUTE
        ) {
            BookSearchScreen(navController, bookViewModel)
        }

        composable(
            route = NavigateDestinations.BOOK_DETAILS_ROUTE + "/{bookUrl}",
            arguments = listOf(navArgument("bookUrl") { type = NavType.StringType })
        ) { backStackEntry ->
            val bookUrl = backStackEntry.arguments!!.getString("bookUrl")!!
            BookScreen(bookUrl, bookViewModel)
        }

        composable(
            route = NavigateDestinations.BOOK_RESULTS_ROUTE + "?query={query}&searchtype={searchtype}",
            arguments = listOf(
                navArgument("query") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("searchtype") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val query = backStackEntry.arguments?.getString("query")
            val searchType = backStackEntry.arguments?.getString("searchtype")
            BookResultsScreen(navController, bookViewModel, query, searchType)
        }

    }
}