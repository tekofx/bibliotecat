package dev.tekofx.biblioteques.model.library

import java.time.LocalDate

data class DateInterval(
    val from: LocalDate, val to: LocalDate
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DateInterval

        if (from != other.from) return false
        if (to != other.to) return false

        return true
    }

    override fun hashCode(): Int {
        var result = from.hashCode()
        result = 31 * result + to.hashCode()
        return result
    }
}