package dev.tekofx.biblioteques.ui.screens.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import dev.tekofx.biblioteques.components.library.ContactType
import dev.tekofx.biblioteques.components.library.LibraryContactCard
import dev.tekofx.biblioteques.model.library.Library

@Composable
fun LibraryContact(library: Library) {

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        library.emails.forEach {
            LibraryContactCard(
                contactType = ContactType.mail,
                text = it
            )
        }

        library.phones.forEach {
            LibraryContactCard(
                contactType = ContactType.phone,
                text = it
            )
        }
    }

}

