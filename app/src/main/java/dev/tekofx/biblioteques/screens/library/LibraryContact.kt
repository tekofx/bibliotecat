package dev.tekofx.biblioteques.screens.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import dev.tekofx.biblioteques.components.library.LibraryContactCard
import dev.tekofx.biblioteques.model.Library
import dev.tekofx.biblioteques.ui.IconResource

@Composable
fun LibraryContact(library: Library) {

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        library.emails.forEach {
            LibraryContactCard(
                iconResource = IconResource.fromImageVector(Icons.Outlined.MailOutline),
                text = it
            )
        }

        library.phones.forEach {
            LibraryContactCard(
                iconResource = IconResource.fromImageVector(Icons.Outlined.Call),
                text = it
            )
        }
    }

}