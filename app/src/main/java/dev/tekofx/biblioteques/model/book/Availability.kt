package dev.tekofx.biblioteques.model.book

import dev.tekofx.biblioteques.model.StatusColor

class Availability(
    val text: String
) {
    enum class Value {
        AVAILABLE,
        CAN_RESERVE,
        NOT_AVAILABLE
    }

    var value: Value
    var color: StatusColor

    init {
        val (value, color) = when {
            text.contains(
                "Disponible",
                ignoreCase = true
            ) -> Value.AVAILABLE to StatusColor.GREEN

            text.contains(
                "Prestatge reserva",
                ignoreCase = true
            ) -> Value.CAN_RESERVE to StatusColor.YELLOW

            text.contains(
                "Venç el",
                ignoreCase = true
            ) -> Value.CAN_RESERVE to StatusColor.YELLOW

            text.contains(
                "En trànsit",
                ignoreCase = true
            ) -> Value.CAN_RESERVE to StatusColor.YELLOW

            text.contains(
                "IR PENDENT",
                ignoreCase = true
            ) -> Value.CAN_RESERVE to StatusColor.YELLOW

            else -> Value.NOT_AVAILABLE to StatusColor.RED
        }

        this.value = value
        this.color = color
    }
}