package dev.tekofx.bibliotecat.model.library

import dev.tekofx.bibliotecat.utils.formatDayOfWeek
import java.time.DayOfWeek
import java.time.LocalDate

/**
 * Represents a timetable for a period, containing day-specific timetables.
 *
 * @property start The start date of the timetable period.
 * @property end The end date of the timetable period.
 * @property dayTimetables A map of day-specific timetables.
 */
data class SeasonTimeTable(
    val start: LocalDate,
    val end: LocalDate,
    val dayTimetables: Map<DayOfWeek, DayTimeTable>,
    val season: Season,
    val observation: String?,
) {

    var open = false

    init {
        for (daytimetable in dayTimetables) {
            if (daytimetable.value.open) {
                open = true
            }
        }

    }

    override fun toString(): String {
        var output = "$season (${start} - ${end})\n"
        for (day in dayTimetables) {
            output += "${formatDayOfWeek(day.key)}: ${day.value}\n"
        }
        return output
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SeasonTimeTable

        if (start != other.start) return false
        if (end != other.end) return false
        if (dayTimetables != other.dayTimetables) return false
        if (season != other.season) return false
        if (open != other.open) return false

        return true
    }

    override fun hashCode(): Int {
        var result = start.hashCode()
        result = 31 * result + end.hashCode()
        result = 31 * result + dayTimetables.hashCode()
        result = 31 * result + season.hashCode()
        result = 31 * result + open.hashCode()
        return result
    }

}