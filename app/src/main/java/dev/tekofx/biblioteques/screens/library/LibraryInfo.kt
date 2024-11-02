package dev.tekofx.biblioteques.screens.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.tekofx.biblioteques.R
import dev.tekofx.biblioteques.components.library.LibraryTimeTable
import dev.tekofx.biblioteques.model.Library
import dev.tekofx.biblioteques.model.LibraryDummy

@Composable
fun LibraryInfo(library: Library) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically

        ) {

            Icon(
                painter = painterResource(R.drawable.clock),
                contentDescription = "test",
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(text = "Horaris")


        }
        LibraryTimeTable(library)
    }

}

@Preview
@Composable
fun LibraryInfoPreview() {
    LibraryInfo(LibraryDummy)
}