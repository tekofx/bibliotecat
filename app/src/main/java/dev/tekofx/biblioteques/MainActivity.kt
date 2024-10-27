package dev.tekofx.biblioteques

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import dev.tekofx.biblioteques.call.LibraryService
import dev.tekofx.biblioteques.components.BottomNavigation
import dev.tekofx.biblioteques.navigation.Navigation
import dev.tekofx.biblioteques.repository.LibraryRepository
import dev.tekofx.biblioteques.ui.home.HomeViewModel
import dev.tekofx.biblioteques.ui.home.HomeViewModelFactory

class MainActivity : AppCompatActivity() {
    val libraryRepository = LibraryRepository(LibraryService.getInstance())
    val homeViewModel = HomeViewModelFactory(libraryRepository).create(HomeViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background

            ) {
                MainScreen(homeViewModel)
            }

        }

    }
}

@Composable
fun MainScreen(homeViewModel: HomeViewModel) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            BottomNavigation(navHostController = navController)
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Navigation(navController, homeViewModel)
        }

    }
}