package dev.tekofx.biblioteques.model.library

/**
 * Represents a timetable for a specific day, containing multiple intervals.
 *
 * @property intervals A list of time intervals for the day.
 */
data class DayTimeTable(val intervals: List<Interval>) {

    var open = false

    init {
        for (interval in intervals) {
            if (!interval.isNull()) {
                open = true

            }
        }
    }

    override fun toString(): String {
        var output = ""
        if (intervals.isEmpty()) {
            output += "Tancat"
        } else {

            output += intervals.joinToString(", ")
        }
        return output
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DayTimeTable

        if (intervals != other.intervals) return false
        if (open != other.open) return false

        return true
    }

    override fun hashCode(): Int {
        var result = intervals.hashCode()
        result = 31 * result + open.hashCode()
        return result
    }

}