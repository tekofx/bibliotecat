package dev.tekofx.biblioteques.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import dev.tekofx.biblioteques.R
import dev.tekofx.biblioteques.call.LibraryService
import dev.tekofx.biblioteques.model.Library
import dev.tekofx.biblioteques.repository.LibraryRepository
import dev.tekofx.biblioteques.ui.home.HomeViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun HomeScreen() {
    val libraryRepository = LibraryRepository(LibraryService.getInstance())
    val homeViewModel = HomeViewModel(libraryRepository)
    Column(Modifier.fillMaxSize()) {
        LibraryList(homeViewModel)
        DisposableEffect(Unit) {
            homeViewModel.getLibraries()
            onDispose {}
        }
        Button(onClick = { /* Handle click */ }, Modifier.fillMaxWidth()) {
            Text(text = "Button")
        }
    }
}


@Composable
fun ListItem(data: Library?, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth()) {
        if (data != null) {
            Text(text = data.adrecaNom)
        }
    }
}

@Composable
fun LibraryList(viewModel: HomeViewModel) {
    val libraries by viewModel.libraries.observeAsState(emptyList())
    val listState = rememberLazyListState()

    LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
        items(libraries) { library ->
            LibraryItemComposable(library)
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .distinctUntilChanged()
            .collect { index ->
                if (index + 1 == libraries.size) {
                    viewModel.getLibraries() // Llama a getLibraries() para cargar más datos
                }
            }
    }

}

@Composable
fun LibraryItemComposable(library: Library) {
    val date = LocalDate.now()
    val time = LocalTime.now()
    var color = R.color.red_closed
    if (library.isOpen(date, time)) {
        color = R.color.green_open
        if (library.isClosingSoon(date, time)) {
            color = R.color.yellow_soon
        }
    }


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberImagePainter(library.imatge), // Ajusta con tu imagen
            contentDescription = null,
            modifier = Modifier
                .size(100.dp)
                .padding(8.dp),
            contentScale = ContentScale.Crop
        )
        Column(modifier = Modifier.padding(start = 8.dp)) {
            Text(
                text = library.adrecaNom,
                fontSize = 20.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(text = library.municipiNom, modifier = Modifier.padding(top = 8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.circle),
                    contentDescription = null,
                    modifier = Modifier
                        .size(20.dp)
                        .padding(end = 8.dp),
                    colorFilter = ColorFilter.tint(colorResource(id = color))
                )
                Text(text = library.generateStateMessage(date, time))
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun PreviewLibraryItemComposable() {
//    LibraryItemComposable(
//        library = Library(
//            adrecaNom = "Sample Address",
//            municipiNom = "Sample Municipality",
//            puntId = "2",
//            bibliotecaVirtualUrl = "gas",
//            summerTimeTable =
//        )
//    )
//}