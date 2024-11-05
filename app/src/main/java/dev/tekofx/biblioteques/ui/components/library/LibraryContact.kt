package dev.tekofx.biblioteques.ui.components.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import dev.tekofx.biblioteques.model.library.Library
import dev.tekofx.biblioteques.ui.components.ContactType
import dev.tekofx.biblioteques.ui.components.InfoIntentCard

@Composable
fun LibraryContact(library: Library) {

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        library.emails.forEach {
            InfoIntentCard(
                contactType = ContactType.MAIL,
                text = it
            )
        }

        library.phones.forEach {
            InfoIntentCard(
                contactType = ContactType.PHONE,
                text = it
            )
        }

        if (library.webUrl.isNotEmpty()) {
            InfoIntentCard(
                ContactType.WEB,
                library.webUrl
            )
        }
    }

}

