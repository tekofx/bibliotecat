package dev.tekofx.biblioteques.screens.library

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import dev.tekofx.biblioteques.R
import dev.tekofx.biblioteques.model.Library
import dev.tekofx.biblioteques.model.LibraryDummy
import java.time.LocalDate

@Composable
fun LibraryInfo(library: Library) {
    Row {

        Icon(
            painter = painterResource(R.drawable.clock),
            contentDescription = "test",
        )
        Text(text = "Horaris")

    }

}

@Preview
@Composable
fun LibraryInfoPreview() {
    LibraryInfo(LibraryDummy)
}