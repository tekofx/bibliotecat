package dev.tekofx.bibliotecat.model.library

import dev.tekofx.bibliotecat.model.StatusColor

data class LibraryStatus(
    var value: Value = Value.Closed.closedTemporarily,
    var color: StatusColor = StatusColor.RED,
    var message: String = ""
) {
    sealed class Value {
        sealed class Open : Value() {
            data object open : Open()
            data object closingSoon : Open()
        }

        sealed class Closed : Value() {
            data object openAfternoon : Closed()
            data object openTomorrow : Closed()
            data object openInDays : Closed()
            data object closedTemporarily : Closed()
        }

        sealed class MayBeOpen : Value() {
            object Holiday : MayBeOpen()

            // When can't extract the hours from the API
            object Unknow : MayBeOpen()


        }
    }
}