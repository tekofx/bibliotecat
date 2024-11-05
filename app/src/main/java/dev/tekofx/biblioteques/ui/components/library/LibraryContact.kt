package dev.tekofx.biblioteques.ui.components.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import dev.tekofx.biblioteques.model.library.Library

@Composable
fun LibraryContact(library: Library) {

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        library.emails.forEach {
            LibraryContactCard(
                contactType = ContactType.MAIL,
                text = it
            )
        }

        library.phones.forEach {
            LibraryContactCard(
                contactType = ContactType.PHONE,
                text = it
            )
        }

        LibraryContactCard(
            ContactType.WEB,
            library.webUrl
        )
    }

}

