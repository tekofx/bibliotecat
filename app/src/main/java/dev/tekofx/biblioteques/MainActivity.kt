package dev.tekofx.biblioteques

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.tekofx.biblioteques.call.BookService
import dev.tekofx.biblioteques.call.LibraryService
import dev.tekofx.biblioteques.navigation.Navigation
import dev.tekofx.biblioteques.navigation.showBottomAppBar
import dev.tekofx.biblioteques.repository.BookRepository
import dev.tekofx.biblioteques.repository.LibraryRepository
import dev.tekofx.biblioteques.repository.PreferencesRepository
import dev.tekofx.biblioteques.ui.components.BottomNavigationBar
import dev.tekofx.biblioteques.ui.theme.MyApplicationTheme
import dev.tekofx.biblioteques.ui.viewModels.book.BookViewModel
import dev.tekofx.biblioteques.ui.viewModels.book.BookViewModelFactory
import dev.tekofx.biblioteques.ui.viewModels.library.LibraryViewModel
import dev.tekofx.biblioteques.ui.viewModels.library.LibraryViewModelFactory
import dev.tekofx.biblioteques.ui.viewModels.preferences.PreferencesViewModel
import dev.tekofx.biblioteques.ui.viewModels.preferences.PreferencesViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "setting"
)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val libraryViewModel = ViewModelProvider(
            this,
            LibraryViewModelFactory(LibraryRepository(LibraryService.getInstance()))
        )[LibraryViewModel::class.java]

        val bookViewModel = ViewModelProvider(
            this,
            BookViewModelFactory(BookRepository(BookService.getInstance()))
        )[BookViewModel::class.java]

        val preferencesViewModel = ViewModelProvider(
            this,
            PreferencesViewModelFactory(PreferencesRepository(dataStore))
        )[PreferencesViewModel::class.java]


        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {

                Surface(
                    modifier = Modifier
                        .fillMaxSize(),
                    color = MaterialTheme.colorScheme.background

                ) {
                    MainScreen(libraryViewModel, bookViewModel, preferencesViewModel)
                }
            }


        }

    }
}

@Composable
fun MainScreen(
    libraryViewModel: LibraryViewModel,
    bookViewModel: BookViewModel,
    preferencesViewModel: PreferencesViewModel
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route


    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
                visible = showBottomAppBar(currentRoute)
            ) {
                BottomNavigationBar(navHostController = navController)
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Navigation(navController, libraryViewModel, bookViewModel, preferencesViewModel)
        }

    }
}