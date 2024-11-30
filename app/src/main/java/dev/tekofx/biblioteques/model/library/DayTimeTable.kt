package dev.tekofx.biblioteques.model.library

/**
 * Represents a timetable for a specific day, containing multiple intervals.
 *
 * @property timeIntervals A list of time intervals for the day.
 */
data class DayTimeTable(val timeIntervals: List<TimeInterval>) {

    var open = false

    init {
        for (interval in timeIntervals) {
            if (!interval.isNull()) {
                open = true

            }
        }
    }

    override fun toString(): String {
        var output = ""
        if (timeIntervals.isEmpty()) {
            output += "Tancat"
        } else {

            output += timeIntervals.joinToString(", ")
        }
        return output
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DayTimeTable

        if (timeIntervals != other.timeIntervals) return false
        if (open != other.open) return false

        return true
    }

    override fun hashCode(): Int {
        var result = timeIntervals.hashCode()
        result = 31 * result + open.hashCode()
        return result
    }

}