package dev.tekofx.biblioteques.screens.library


import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import dev.tekofx.biblioteques.call.LibraryService
import dev.tekofx.biblioteques.components.OpeningStatus
import dev.tekofx.biblioteques.components.TabRowComponent
import dev.tekofx.biblioteques.repository.LibraryRepository
import dev.tekofx.biblioteques.ui.home.HomeViewModel
import dev.tekofx.biblioteques.ui.home.HomeViewModelFactory

val tabs = listOf("Info", "Contacta")

@Composable
fun LibraryScreen(
    pointID: String, homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(
            LibraryRepository(LibraryService.getInstance())
        )
    )
) {
    Log.d("LibraryScreen", "Navigated to $pointID")
    val currentLibrary by homeViewModel.currentLibrary.observeAsState(null)
    val isLoading by homeViewModel.isLoading.observeAsState(false)
    LaunchedEffect(key1 = Unit) {
        homeViewModel.getLibrary(pointID)
    }
    Column {

        currentLibrary?.let { library ->
            // Use the library object here
            Column {

                AsyncImage(
                    model = library.imatge, // Ajusta con tu imagen
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1F)
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop
                )
                Text(text = library.adrecaNom, fontSize = 30.sp)
                Text(text = library.municipiNom, fontSize = 26.sp)
                OpeningStatus(library)
                TabRowComponent(
                    tabs = tabs,
                    contentScreens = listOf(
                        { LibraryInfo() },  // Content screen for Tab 1
                        { LibraryContact() },      // Content screen for Tab 2
                    ),
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color.Gray,
                    contentColor = Color.White,
                    indicatorColor = Color.DarkGray
                )
            }
        } ?: run {
            if (isLoading) {
                CircularProgressIndicator()
            } else {

                Text(text = "No library selected")
            }
        }

    }
}

//@Preview
//@Composable
//fun LibraryScreenTest() {
//    LibrariesScreen(LibraryDummy)
//}