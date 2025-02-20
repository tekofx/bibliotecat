package dev.tekofx.biblioteques.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import dev.tekofx.biblioteques.R
import dev.tekofx.biblioteques.navigation.NavigateDestinations
import dev.tekofx.biblioteques.ui.IconResource
import dev.tekofx.biblioteques.ui.viewModels.library.LibraryViewModel

@Composable
fun LoadingScreen(
    navHostController: NavHostController,
    libraryViewModel: LibraryViewModel
) {

    val isLoading by libraryViewModel.isLoading.collectAsState()

    LaunchedEffect(key1 = isLoading) {
        if (!isLoading) {
            navHostController.popBackStack()
            navHostController.navigate(NavigateDestinations.LIBRARIES_ROUTE)
        }

        //delay(1500)
    }
    Welcome()
}

@Composable
fun Welcome() {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = IconResource.fromDrawableResource(R.drawable.ic_launcher_foreground)
                    .asPainterResource(),
                contentDescription = ""
            )
            CircularProgressIndicator()
        }
    }
}


@Preview
@Composable
fun WelcomePreview() {
    Welcome()
}