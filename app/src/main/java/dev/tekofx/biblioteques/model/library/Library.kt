package dev.tekofx.biblioteques.model.library

import dev.tekofx.biblioteques.utils.formatDayOfWeek
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime


/**
 * Represents a time interval with a start and end time.
 *
 * @property from The start time of the interval.
 * @property to The end time of the interval.
 */
data class Interval(val from: LocalTime?, val to: LocalTime?, val observation: String? = null) {

    fun isNull(): Boolean {
        return from == null || to == null
    }

    override fun toString(): String {
        if (this.from == null && observation == null) {
            return "Tancat"
        }

        var output = "$from - $to"

        if (observation != null) {
            output += " ($observation)"
        }

        return output
    }
}

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
}

enum class Season {
    WINTER,
    SUMMER
}

val seasonTranslation = mapOf(
    Season.WINTER to "Hivern",
    Season.SUMMER to "Estiu"
)

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
    val dayTimetables: Map<DayOfWeek, DayTimeTable>,
    val season: Season
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
        var output = "(${start} - ${end})\n"
        for (day in dayTimetables) {
            output += "${formatDayOfWeek(day.key)}: ${day.value}\n"
        }
        return output
    }
}

/**
 * Represents a library with summer and winter timetables.
 *
 * @property id The ID of the library.
 * @property adrecaNom The name of the library.
 * @property description A description of the library.
 * @property municipality The name of the municipality.
 * @property address The complete address of the library
 * @property bibliotecaVirtualUrl The URL of the bibliotecavirtual.diba.cat/
 * @property emails A list of email addresses associated with the library.
 * @property phones A list of phones associated with the library.
 * @property image The URL of the library's image.
 * @property summerTimeTable The timetable for the summer period.
 * @property winterTimetable The timetable for the winter period.
 */
class Library(
    val id: String,
    val adrecaNom: String,
    val description: String,
    val municipality: String,
    val address: String,
    val bibliotecaVirtualUrl: String?,
    val emails: List<String>,
    val phones: List<String>,
    var image: String,
    val summerTimeTable: TimeTable,
    val winterTimetable: TimeTable,
) {


    /**
     * Checks if the library is open at a given date and time.
     *
     * @param date The date to check.
     * @param hora The time to check.
     * @return True if the library is open, false otherwise.
     */
    fun isOpen(date: LocalDate, hora: LocalTime): Boolean {
        val currentTimetable = getCurrentSeasonTimetable(date)
        val dayTimetable = currentTimetable.dayTimetables[date.dayOfWeek]
        return dayTimetable?.intervals?.any { interval ->
            hora.isAfter(interval.from) && hora.isBefore(interval.to)
        } ?: false
    }

    /**
     * Checks if the library is closing in an hour or less at a given date and time.
     * @param date The date to check.
     * @param time The time to check.
     * @return True if the library is closing in less than an hour, false otherwise.
     */
    fun isClosingSoon(date: LocalDate, time: LocalTime): Boolean {
        val currentInterval = getCurrentInterval(date, time)!!
        return Duration.between(time, currentInterval.to).toMinutes() <= 60
    }

    /**
     * Gets the current timetable based on the given date.
     *
     * @param date The date to check.
     * @return The current timetable.
     */
    fun getCurrentSeasonTimetable(date: LocalDate): TimeTable {
        return if (date == summerTimeTable.start || date == summerTimeTable.end) {
            summerTimeTable
        } else if (date.isAfter(summerTimeTable.start) && date.isBefore(summerTimeTable.end)) {
            return summerTimeTable
        } else {
            winterTimetable
        }
    }

    /**
     * Gets the next season timetable based on the given date.
     *
     * @param date The date to check.
     * @return The next season timetable.
     */
    fun getNextSeasonTimetable(date: LocalDate): TimeTable {
        if (getCurrentSeasonTimetable(date) == summerTimetable) {
            return winterTimetable
        }
        return summerTimetable
    }

    /**
     * Gets the current day's timetable based on the given date.
     *
     * @param date The date to check.
     * @return The current day's timetable, or null if it doesn't exist.
     */
    fun getCurrentDayTimetable(date: LocalDate): DayTimeTable? {
        val currentTimetable = getCurrentSeasonTimetable(date)
        return currentTimetable.dayTimetables[date.dayOfWeek]
    }

    /**
     * Gets the next day the library is open after the given date.
     *
     * @param date The date to start checking from.
     * @return The next date the library is open.
     */
    fun getNextDayOpen(date: LocalDate): LocalDate? {
        var nextDay = date.plusDays(1)
        var currentTimetable = getCurrentSeasonTimetable(nextDay)

        if (!summerTimeTable.open && !winterTimetable.open) {
            return null
        }

        while (currentTimetable.dayTimetables[nextDay.dayOfWeek]?.intervals.isNullOrEmpty()) {
            nextDay = nextDay.plusDays(1)
            currentTimetable = getCurrentSeasonTimetable(nextDay)
        }

        return nextDay
    }

    /**
     * Gets the current Interval
     * @param date The date to check.
     * @param time The time to check.
     * @return The current interval, or null if it doesn't exist.
     */
    fun getCurrentInterval(date: LocalDate, time: LocalTime): Interval? {
        val currentTimetable = getCurrentSeasonTimetable(date)
        val dayTimeTable = currentTimetable.dayTimetables[date.dayOfWeek]
        return dayTimeTable?.intervals?.find { interval ->
            time == interval.from || (time.isAfter(interval.from) && time.isBefore(interval.to))
        }
    }

    /**
     * Gets the next interval of day based on the given date and time.
     * @param date The date to check.
     * @param time The time to check.
     * @return The next interval of day, or null if it doesn't exist.
     */
    fun getNextIntervalOfDay(date: LocalDate, time: LocalTime): Interval? {
        val dayTimeTable = getCurrentDayTimetable(date)
        val nextInterval = dayTimeTable?.intervals?.find { interval ->
            time.isBefore(interval.from)
        }

        return nextInterval

    }


    /**
     * Generates a state message indicating whether the library is open or closed.
     *
     * @param date The date to check.
     * @param time The time to check.
     * @return A message indicating the state of the library.
     */
    fun generateStateMessage(date: LocalDate, time: LocalTime): String {
        if (isOpen(date, time)) {
            val currentInterval = getCurrentInterval(date, time)!!

            if (isClosingSoon(date, time)) {
                return "Obert · Tanca a les ${currentInterval.to}"
            }


            return "Obert · Fins a ${currentInterval.to}"
        } else {
            // Verify if there are more intervals in the same day
            val nextIntervalOfDay = getNextIntervalOfDay(date, time)
            if (nextIntervalOfDay != null) {
                return "Tancat · Obre a las ${nextIntervalOfDay.from}"
            }

            // If there are no more intervals today, look for the next opening day
            // If next day null means Permanently closed
            val nextDay = getNextDayOpen(date) ?: return "Tancat Permanentment"


            val nextTimetable = getCurrentSeasonTimetable(nextDay)
            val nextDayTimetable = nextTimetable.dayTimetables[nextDay.dayOfWeek]
            val nextDayName = formatDayOfWeek(nextDay.dayOfWeek)
            // Opens tomorrow
            if (nextDay == date.plusDays(1)) {
                return "Tancat · Obre demà a las ${nextDayTimetable?.intervals?.firstOrNull()?.from}"

            }
            return "Tancat · Obre el $nextDayName a las ${nextDayTimetable?.intervals?.firstOrNull()?.from}"
        }
    }


    override fun toString(): String {
        var output = "------------------------------------------------------\n"
        output += "$adrecaNom - ${municipality}\nWinterTimetable: ${winterTimetable}\nSummerTimetable ${summerTimeTable}"
        output += "------------------------------------------------------"
        return output
    }


}

data class DateInterval(
    val from: LocalDate, val to: LocalDate
)