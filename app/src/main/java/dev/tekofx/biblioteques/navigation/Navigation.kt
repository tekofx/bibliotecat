package dev.tekofx.biblioteques.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.tekofx.biblioteques.screens.DashboardScreen
import dev.tekofx.biblioteques.screens.HomeScreen
import dev.tekofx.biblioteques.ui.home.HomeViewModel

@Composable
fun Navigation(navController: NavHostController, homeViewModel: HomeViewModel) {
    NavHost(navController = navController, startDestination = NavScreen.HomeScreen.name) {
        composable(NavScreen.HomeScreen.name) {
            HomeScreen(homeViewModel)
        }

        composable(NavScreen.BottomNavigationItems.name) {
            DashboardScreen()
        }

    }
}