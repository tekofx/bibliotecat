package dev.tekofx.biblioteques.model.library

import dev.tekofx.biblioteques.model.StatusColor


sealed class OpenStatusEnum {
    sealed class Open : OpenStatusEnum() {
        object open : Open()
        object closingSoon : Open()
    }

    sealed class Closed : OpenStatusEnum() {
        object openInAfternoon : Closed()
        object openTomorrow : Closed()
        object openInDays : Closed()
        object closedTemporarily : Closed()
        object holiday : Closed()
        object closed : Closed()
    }
}

data class OpenStatus(
    var status: OpenStatusEnum = OpenStatusEnum.Closed.closed,
    var color: StatusColor = StatusColor.RED,
    var message: String = ""
)