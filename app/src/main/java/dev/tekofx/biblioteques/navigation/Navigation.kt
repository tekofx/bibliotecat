package dev.tekofx.biblioteques.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import dev.tekofx.biblioteques.ui.screens.AboutScreen
import dev.tekofx.biblioteques.ui.screens.LoadingScreen
import dev.tekofx.biblioteques.ui.screens.MapScreen
import dev.tekofx.biblioteques.ui.screens.SettingsScreen
import dev.tekofx.biblioteques.ui.screens.WelcomeScreen
import dev.tekofx.biblioteques.ui.screens.book.BookResultsScreen
import dev.tekofx.biblioteques.ui.screens.book.BookScreen
import dev.tekofx.biblioteques.ui.screens.book.BookSearchScreen
import dev.tekofx.biblioteques.ui.screens.library.LibrariesScreen
import dev.tekofx.biblioteques.ui.screens.library.LibraryScreen
import dev.tekofx.biblioteques.ui.viewModels.book.BookViewModel
import dev.tekofx.biblioteques.ui.viewModels.library.LibraryViewModel
import dev.tekofx.biblioteques.ui.viewModels.preferences.PreferencesViewModel


@Composable
fun Navigation(
    navHostController: NavHostController,
    libraryViewModel: LibraryViewModel,
    bookViewModel: BookViewModel,
    preferencesViewModel: PreferencesViewModel
) {

    val preferences by preferencesViewModel.uiState.collectAsState()

    NavHost(
        navController = navHostController,
        //startDestination = if (preferences.showTutorial) NavigateDestinations.TUTORIAL_SCREEN else NavigateDestinations.TUTORIAL_SCREEN,
        startDestination = if (preferences.showWelcomeScreen) NavigateDestinations.WELCOME_SCREEN else NavigateDestinations.LOADING_SCREEN
    ) {

        // Tutorial
        composable(
            route = NavigateDestinations.WELCOME_SCREEN,
            exitTransition = { fadeOut() }
        ) {
            WelcomeScreen(navHostController, preferencesViewModel)
        }

        // Welcome
        composable(
            route = NavigateDestinations.LOADING_SCREEN,
            exitTransition = { fadeOut() }
        ) {
            LoadingScreen(navHostController, libraryViewModel)
        }

        // Libraries
        composable(
            route = NavigateDestinations.LIBRARIES_ROUTE,
            //enterTransition = ::slideInToRight,
            enterTransition = {
                // If previous screen was BookSearchScreen
                if (initialState.destination.route == NavigateDestinations.BOOK_SEARCH_ROUTE) {
                    slideInToRight(this)
                } else {
                    null
                }
            },
            exitTransition = { fadeOut() },
        ) {
            LibrariesScreen(navHostController, libraryViewModel)
        }

        composable(
            route = "${NavigateDestinations.LIBRARY_DETAILS_ROUTE}?pointId={pointId}&libraryUrl={libraryUrl}",
            enterTransition = ::slideInToTop,
            popExitTransition = ::slideOutToBottom,
            arguments = listOf(
                navArgument("pointId") {
                    type = NavType.StringType
                    nullable = true
                },
                navArgument("libraryUrl") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val pointId = backStackEntry.arguments!!.getString("pointId")
            val libraryUrl = backStackEntry.arguments!!.getString("libraryUrl")

            LibraryScreen(
                pointID = pointId,
                libraryUrl = libraryUrl,
                libraryViewModel,
                navHostController
            )
        }
        // Books
        composable(
            route = NavigateDestinations.BOOK_SEARCH_ROUTE,
            popEnterTransition = {
                // If previous screen was LibrariesScreen
                if (initialState.destination.route == NavigateDestinations.LIBRARIES_ROUTE) {
                    slideInToLeft(this)
                } else {
                    slideInToBottom(this)
                }
            },

            enterTransition = ::slideInToLeft,
            exitTransition = {
                // If Next Screen is not LibrariesScreen
                if (targetState.destination.route != NavigateDestinations.LIBRARIES_ROUTE) {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Up,
                    )

                } else {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,
                    )
                }
            }
        ) {
            BookSearchScreen(navHostController, bookViewModel)
        }

        composable(
            route = NavigateDestinations.BOOK_RESULTS_ROUTE,
            enterTransition = ::slideInToTop,
            popEnterTransition = ::slideInToBottom,
            popExitTransition = ::slideOutToBottom,
        ) { backStackEntry ->
            BookResultsScreen(navHostController, bookViewModel)
        }

        composable(
            route = NavigateDestinations.BOOK_DETAILS_ROUTE + "/{bookUrl}",
            enterTransition = ::slideInToTop,
            exitTransition = ::slideOutToBottom,
            popEnterTransition = ::slideInToTop,
            arguments = listOf(navArgument("bookUrl") { type = NavType.StringType })
        ) { backStackEntry ->
            val bookUrl = backStackEntry.arguments!!.getString("bookUrl")!!
            BookScreen(bookUrl, navHostController, bookViewModel)
        }


        composable(
            route = NavigateDestinations.MAP_ROUTE + "?pointId={pointId}",
            enterTransition = ::slideInToTop,
            exitTransition = ::slideOutToBottom,
            popEnterTransition = ::slideInToTop,
            popExitTransition = ::slideOutToBottom,
            arguments = listOf(
                navArgument("pointId") {
                    type = NavType.StringType
                    nullable = false
                }
            )
        )
        { backStackEntry ->
            val pointId = backStackEntry.arguments!!.getString("pointId")!!
            MapScreen(pointId, libraryViewModel)
        }

        // Settings and About

        composable(
            route = NavigateDestinations.SETTINGS_ROUTE,
            enterTransition = ::slideInToTop,
            exitTransition = ::slideOutToBottom
        )
        {
            SettingsScreen()
        }

        composable(
            route = NavigateDestinations.ABOUT_ROUTE,
            enterTransition = ::slideInToTop,
            exitTransition = ::slideOutToBottom
        )
        {
            AboutScreen()
        }

    }
}

// SlideIn Transitions
fun slideInToTop(scope: AnimatedContentTransitionScope<NavBackStackEntry>): EnterTransition {
    return scope.slideIntoContainer(
        AnimatedContentTransitionScope.SlideDirection.Up,
    )
}

fun slideInToBottom(scope: AnimatedContentTransitionScope<NavBackStackEntry>): EnterTransition {
    return scope.slideIntoContainer(
        AnimatedContentTransitionScope.SlideDirection.Down,
    )
}


fun slideInToRight(scope: AnimatedContentTransitionScope<NavBackStackEntry>): EnterTransition {
    return scope.slideIntoContainer(
        AnimatedContentTransitionScope.SlideDirection.Right,
    )
}

fun slideInToLeft(scope: AnimatedContentTransitionScope<NavBackStackEntry>): EnterTransition {
    return scope.slideIntoContainer(
        AnimatedContentTransitionScope.SlideDirection.Left,
    )
}

// SlideOut Transitions

fun slideOutToTop(scope: AnimatedContentTransitionScope<NavBackStackEntry>): ExitTransition {
    return scope.slideOutOfContainer(
        AnimatedContentTransitionScope.SlideDirection.Up,
    )
}

fun slideOutToBottom(scope: AnimatedContentTransitionScope<NavBackStackEntry>): ExitTransition {
    return scope.slideOutOfContainer(
        AnimatedContentTransitionScope.SlideDirection.Down,
    )
}
