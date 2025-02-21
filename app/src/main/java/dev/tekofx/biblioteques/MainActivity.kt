package dev.tekofx.biblioteques

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.tekofx.biblioteques.call.BookService
import dev.tekofx.biblioteques.call.HolidayService
import dev.tekofx.biblioteques.call.LibraryService
import dev.tekofx.biblioteques.navigation.NavigateDestinations
import dev.tekofx.biblioteques.navigation.Navigation
import dev.tekofx.biblioteques.navigation.showBottomAppBar
import dev.tekofx.biblioteques.repository.BookRepository
import dev.tekofx.biblioteques.repository.HolidayRepository
import dev.tekofx.biblioteques.repository.LibraryRepository
import dev.tekofx.biblioteques.repository.PreferencesRepository
import dev.tekofx.biblioteques.ui.IconResource
import dev.tekofx.biblioteques.ui.components.BottomNavigationBar
import dev.tekofx.biblioteques.ui.components.input.TextIconButton
import dev.tekofx.biblioteques.ui.theme.MyApplicationTheme
import dev.tekofx.biblioteques.ui.viewModels.book.BookViewModel
import dev.tekofx.biblioteques.ui.viewModels.book.BookViewModelFactory
import dev.tekofx.biblioteques.ui.viewModels.library.LibraryViewModel
import dev.tekofx.biblioteques.ui.viewModels.library.LibraryViewModelFactory
import dev.tekofx.biblioteques.ui.viewModels.preferences.PreferencesViewModel
import dev.tekofx.biblioteques.ui.viewModels.preferences.PreferencesViewModelFactory
import kotlinx.coroutines.launch

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "setting"
)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val libraryViewModel = ViewModelProvider(
            this,
            LibraryViewModelFactory(
                LibraryRepository(
                    LibraryService.getInstance(),
                    HolidayService.getInstance()
                ),
                HolidayRepository(HolidayService.getInstance())
            )
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
        setContent {
            MyApplicationTheme {
                MainScreen(libraryViewModel, bookViewModel, preferencesViewModel)
                SetNavigationBarColor()
            }


        }


    }
}

@Composable
fun SetNavigationBarColor() {
    val color = MaterialTheme.colorScheme.surface
    val window = (LocalContext.current as Activity).window

    SideEffect {
        window.navigationBarColor = color.toArgb()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsInfoModalSheet(
    show: Boolean,
    onClose: () -> Unit,
    navController: NavHostController
) {

    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    fun close() {
        scope.launch { sheetState.hide() }.invokeOnCompletion {
            if (!sheetState.isVisible) {
                onClose()
            }
        }
    }

    if (show) {
        ModalBottomSheet(
            onDismissRequest = {
                close()
            },
            sheetState = sheetState,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FilledTonalButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        navController.navigate(NavigateDestinations.SETTINGS_ROUTE)
                        close()
                    }
                ) {
                    Text("Settings")
                }
                FilledTonalButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {}
                ) {
                    Text("Info")
                }
                TextIconButton(
                    text = "Tanca",
                    startIcon = IconResource.fromImageVector(Icons.Outlined.Close),
                    onClick = { close() }
                )

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
    var showModalSheet by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        bottomBar = {
            AnimatedVisibility(
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
                visible = showBottomAppBar(currentRoute)
            ) {
                BottomNavigationBar(
                    navHostController = navController,
                    onMenuClick = { showModalSheet = true }
                )
            }
        }
    ) { padding ->
        SettingsInfoModalSheet(
            show = showModalSheet,
            onClose = { showModalSheet = false },
            navController = navController
        )

        Box(
            modifier = Modifier
                .padding(padding)
        ) {
            Navigation(navController, libraryViewModel, bookViewModel, preferencesViewModel)
        }

    }
}