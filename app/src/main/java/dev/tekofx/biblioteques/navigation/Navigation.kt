package dev.tekofx.biblioteques.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.tekofx.biblioteques.screens.DashboardScreen
import dev.tekofx.biblioteques.screens.LibraryScreen

@Composable
fun Navigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = NavScreen.HomeScreen.name) {
        composable(NavScreen.HomeScreen.name) {
            LibraryScreen()
        }

        composable(NavScreen.BottomNavigationItems.name) {
            DashboardScreen()
        }

    }
}