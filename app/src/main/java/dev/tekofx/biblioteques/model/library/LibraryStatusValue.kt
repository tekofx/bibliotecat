package dev.tekofx.biblioteques.model.library

sealed class LibraryStatusValue {
    sealed class Library : LibraryStatusValue() {
        object library : Library()
        object closingSoon : Library()
    }

    sealed class Closed : LibraryStatusValue() {
        object libraryInAfternoon : Closed()
        object libraryTomorrow : Closed()
        object libraryInDays : Closed()
        object closedTemporarily : Closed()
        object holiday : Closed()
    }
}