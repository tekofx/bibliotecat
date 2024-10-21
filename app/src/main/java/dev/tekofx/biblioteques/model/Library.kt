package dev.tekofx.biblioteques.model

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Represents a time interval with a start and end time.
 *
 * @property from The start time of the interval.
 * @property to The end time of the interval.
 */
data class Interval(val from: LocalTime?, val to: LocalTime?, val observation: String? = null)

/**
 * Represents a timetable for a specific day, containing multiple intervals.
 *
 * @property intervals A list of time intervals for the day.
 */
data class DayTimeTable(val intervals: List<Interval>)

/**
 * Represents a timetable for a period, containing day-specific timetables.
 *
 * @property start The start date of the timetable period.
 * @property end The end date of the timetable period.
 * @property dayTimetables A map of day-specific timetables.
 */
data class TimeTable(
    val start: LocalDate,
    val end: LocalDate,
    val dayTimetables: Map<DayOfWeek, DayTimeTable>
)

/**
 * Represents a library with summer and winter timetables.
 *
 * @property puntId The ID of the library.
 * @property adrecaNom The name of the library.
 * @property descripcio A description of the library.
 * @property municipiNom The name of the municipality.
 * @property bibliotecaVirtualUrl The URL of the bibliotecavirtual.diba.cat/
 * @property emails A list of email addresses associated with the library.
 * @property imatge The URL of the library's image.
 * @property summerTimeTable The timetable for the summer period.
 * @property winterTimetable The timetable for the winter period.
 */
class Library(
    val puntId: String,
    val adrecaNom: String,
    val descripcio: String,
    val municipiNom: String,
    val bibliotecaVirtualUrl: String?,
    val emails: List<String>,
    var imatge: String,
    val summerTimeTable: TimeTable,
    val winterTimetable: TimeTable,
) {
    private val dayFormatter = DateTimeFormatter.ofPattern("EEEE", Locale("ca"))

    /**
     * Gets the current timetable based on the given date.
     *
     * @param date The date to check.
     * @return The current timetable.
     */
    fun getCurrentTimetable(date: LocalDate): TimeTable {
        return if (date.isAfter(summerTimeTable.start) && date.isBefore(summerTimeTable.end)) {
            summerTimeTable
        } else {
            winterTimetable
        }
    }

    /**
     * Checks if the library is open at a given date and time.
     *
     * @param date The date to check.
     * @param hora The time to check.
     * @return True if the library is open, false otherwise.
     */
    fun isOpen(date: LocalDate, hora: LocalTime): Boolean {
        val currentTimetable = getCurrentTimetable(date)
        val dayTimetable = currentTimetable.dayTimetables[date.dayOfWeek]
        return dayTimetable?.intervals?.any { interval ->
            hora.isAfter(interval.from) && hora.isBefore(interval.to)
        } ?: false
    }

    /**
     * Generates a state message indicating whether the library is open or closed.
     *
     * @param date The date to check.
     * @param time The time to check.
     * @return A message indicating the state of the library.
     */
    fun generateStateMessage(date: LocalDate, time: LocalTime): String {
        val currentTimetable = getCurrentTimetable(date)
        val dayTimeTable = currentTimetable.dayTimetables[date.dayOfWeek]

        if (isOpen(date, time)) {
            val currentInterval = dayTimeTable?.intervals?.find { interval ->
                time.isAfter(interval.from) && time.isBefore(interval.to)
            }
            return "Obert · Fins a ${currentInterval?.to}"
        } else {
            // Verify if there are more intervals in the same day
            val nextInterval = dayTimeTable?.intervals?.find { interval ->
                time.isBefore(interval.from)
            }
            if (nextInterval != null) {
                return "Tancat · Obre a las ${nextInterval.from}"
            }

            // If there are no more intervals today, look for the next opening day
            val nextDay = getNextDayOpen(date)
            val nextTimetable = getCurrentTimetable(nextDay)
            val nextDayTimetable = nextTimetable.dayTimetables[nextDay.dayOfWeek]
            val nextDayName = nextDay.format(dayFormatter)
            return "Tancat · Obre el $nextDayName a las ${nextDayTimetable?.intervals?.firstOrNull()?.from}"
        }
    }

    /**
     * Gets the next day the library is open after the given date.
     *
     * @param date The date to start checking from.
     * @return The next date the library is open.
     */
    fun getNextDayOpen(date: LocalDate): LocalDate {
        var nextDay = date.plusDays(1)
        var currentTimetable = getCurrentTimetable(nextDay)

        while (currentTimetable.dayTimetables[nextDay.dayOfWeek]?.intervals.isNullOrEmpty()) {
            nextDay = nextDay.plusDays(1)
            currentTimetable = getCurrentTimetable(nextDay)
        }

        return nextDay
    }
}

data class DateInterval(
    val from: LocalDate,
    val to: LocalDate
)