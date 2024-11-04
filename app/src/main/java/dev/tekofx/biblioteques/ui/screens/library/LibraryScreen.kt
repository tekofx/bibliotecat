package dev.tekofx.biblioteques.ui.screens.library


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
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import dev.tekofx.biblioteques.call.LibraryService
import dev.tekofx.biblioteques.repository.LibraryRepository
import dev.tekofx.biblioteques.ui.IconResource
import dev.tekofx.biblioteques.ui.components.TabEntry
import dev.tekofx.biblioteques.ui.components.TabRowComponent
import dev.tekofx.biblioteques.ui.components.library.LibraryContact
import dev.tekofx.biblioteques.ui.components.library.LibraryInfo
import dev.tekofx.biblioteques.ui.components.library.LibraryLocation
import dev.tekofx.biblioteques.ui.components.library.OpeningStatus
import dev.tekofx.biblioteques.ui.theme.Typography
import dev.tekofx.biblioteques.ui.viewModels.library.LibraryViewModel
import dev.tekofx.biblioteques.ui.viewModels.library.LibraryViewModelFactory


val tabEntries = listOf(
    TabEntry("Info", IconResource.fromImageVector(Icons.Outlined.Menu)),
    TabEntry("Com Arribar", IconResource.fromImageVector(Icons.Outlined.LocationOn)),
    TabEntry("Contacta", IconResource.fromImageVector(Icons.Outlined.MailOutline)),
)

@Composable
fun LibraryScreen(
    pointID: String, libraryViewModel: LibraryViewModel = viewModel(
        factory = LibraryViewModelFactory(
            LibraryRepository(LibraryService.getInstance())
        )
    )
) {
    Log.d("LibraryScreen", "Navigated to $pointID")
    val currentLibrary by libraryViewModel.currentLibrary.observeAsState(null)
    val isLoading by libraryViewModel.isLoading.observeAsState(false)
    LaunchedEffect(key1 = Unit) {
        libraryViewModel.getLibrary(pointID)
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

                Text(text = library.adrecaNom, style = Typography.headlineMedium)
                Text(text = library.municipality, style = Typography.headlineSmall)
                OpeningStatus(library, Typography.titleLarge)
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

