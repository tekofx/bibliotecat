package dev.tekofx.biblioteques.screens.library


import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.tekofx.biblioteques.call.LibraryService
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
    val library = homeViewModel.getLibrary(punt_id = pointID)
    if (library != null) {
        Log.d("test2", library.puntId)
    }
    Column {
//        AsyncImage(
//            model = library.imatge, // Ajusta con tu imagen
//            contentDescription = null,
//            modifier = Modifier
//                .size(130.dp)
//                .clip(RoundedCornerShape(10.dp)),
//            contentScale = ContentScale.Crop
//        )
        //Text(text = library.adrecaNom)
//        Text(text = library.municipiNom)
//        OpeningStatus(library)
        Text(text = "hola")
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
}

//@Preview
//@Composable
//fun LibraryScreenTest() {
//    LibrariesScreen(LibraryDummy)
//}