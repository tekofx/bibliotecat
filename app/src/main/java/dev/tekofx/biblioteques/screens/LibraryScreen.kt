package dev.tekofx.biblioteques.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.tekofx.biblioteques.components.OpeningStatus
import dev.tekofx.biblioteques.model.Library
import dev.tekofx.biblioteques.model.LibraryDummy

@Composable
fun LibrariesScreen(library: Library) {
    Column {
        AsyncImage(
            model = library.imatge, // Ajusta con tu imagen
            contentDescription = null,
            modifier = Modifier
                .size(130.dp)
                .clip(RoundedCornerShape(10.dp)),
            contentScale = ContentScale.Crop
        )
        Text(text = library.adrecaNom)
        Text(text = library.municipiNom)
        OpeningStatus(library)
    }
}

@Preview
@Composable
fun LibraryScreenTest() {
    LibrariesScreen(LibraryDummy)
}