package dev.tekofx.biblioteques.model.library

import dev.tekofx.biblioteques.model.StatusColor

data class LibraryStatus(
    var value: Value = Value.Closed.closedTemporarily,
    var color: StatusColor = StatusColor.RED,
    var message: String = ""
) {
    sealed class Value {
        sealed class Open : Value() {
            object open : Open()
            object closingSoon : Open()
        }

        sealed class Closed : Value() {
            object openAfternoon : Closed()
            object openTomorrow : Closed()
            object openInDays : Closed()
            object closedTemporarily : Closed()
            object Holiday : Closed()
        }
    }
}