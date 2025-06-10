package dev.tekofx.bibliotecat.model.library

import java.time.LocalTime


/**
 * Represents a time interval with a start and end time.
 *
 * @property from The start time of the interval.
 * @property to The end time of the interval.
 */
data class TimeInterval(val from: LocalTime?, val to: LocalTime?, val observation: String? = null) {

    fun isNull(): Boolean {
        return from == null || to == null
    }

    override fun toString(): String {
        if (this.from == null && observation == null) {
            return "Tancat"
        }
        var output = ""
        if (from != null && to != null) {
            output = "$from - $to"
        }

        if (observation != null) {
            output += " ($observation)"
        }

        return output
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TimeInterval
        if (from != other.from) return false
        if (to != other.to) return false
        if (observation != other.observation) return false

        return true
    }

    override fun hashCode(): Int {
        var result = from?.hashCode() ?: 0
        result = 31 * result + (to?.hashCode() ?: 0)
        result = 31 * result + (observation?.hashCode() ?: 0)
        return result
    }
}