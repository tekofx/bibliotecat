package dev.tekofx.biblioteques.screens.library


import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import dev.tekofx.biblioteques.call.LibraryService
import dev.tekofx.biblioteques.components.TabEntry
import dev.tekofx.biblioteques.components.TabRowComponent
import dev.tekofx.biblioteques.components.library.OpeningStatus
import dev.tekofx.biblioteques.repository.LibraryRepository
import dev.tekofx.biblioteques.ui.IconResource
import dev.tekofx.biblioteques.ui.home.HomeViewModel
import dev.tekofx.biblioteques.ui.home.HomeViewModelFactory


val tabEntries = listOf(
    TabEntry("Info", IconResource.fromImageVector(Icons.Outlined.Menu)),
    TabEntry("Com Arribar", IconResource.fromImageVector(Icons.Outlined.LocationOn)),
    TabEntry("Contacta", IconResource.fromImageVector(Icons.Outlined.MailOutline)),
)

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

    currentLibrary?.let { library ->
        // Use the library object here
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(bottom = 10.dp)
        ) {

            AsyncImage(
                model = library.image, // Ajusta con tu imagen
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2F)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(top = 20.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)


            ) {

                Text(text = library.adrecaNom, fontSize = 30.sp)
                Text(text = library.municipality, fontSize = 26.sp)
                OpeningStatus(library)
                TabRowComponent(
                    tabEntries = tabEntries,
                    contentScreens = listOf(
                        { LibraryInfo(library) },
                        { LibraryLocation(library) },
                        { LibraryContact(library) },
                    ),
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    } ?: run {
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),

                ) {

                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        } else {

            Text(text = "No library selected")
        }
    }
}

