package dev.tekofx.biblioteques.ui.components.library

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import dev.tekofx.biblioteques.model.library.Library
import dev.tekofx.biblioteques.model.library.LibraryDummy
import dev.tekofx.biblioteques.ui.components.ContactType
import dev.tekofx.biblioteques.ui.components.InfoIntentCard

@Composable
fun LibraryLocation(library: Library) {
    InfoIntentCard(ContactType.LOCATION, library.address)
}


@Preview
@Composable
fun LibraryContactPreview() {
    LibraryLocation(LibraryDummy)
}