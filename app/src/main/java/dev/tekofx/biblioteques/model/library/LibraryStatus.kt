package dev.tekofx.biblioteques.model.library

import dev.tekofx.biblioteques.model.StatusColor

data class LibraryStatus(
    var value: LibraryStatusValue = LibraryStatusValue.Closed.libraryInDays,
    var color: StatusColor = StatusColor.RED,
    var message: String = ""
)