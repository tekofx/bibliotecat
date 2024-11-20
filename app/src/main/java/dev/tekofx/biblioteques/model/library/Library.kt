package dev.tekofx.biblioteques.model.library

import dev.tekofx.biblioteques.model.StatusColor
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Interval
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TimeTable

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
    val webUrl: String,
    var image: String,
    val summerTimeTable: TimeTable,
    val winterTimetable: TimeTable,
) {

    val currentSeasonTimetable: TimeTable
    val nextSeasonTimeTables: TimeTable

    init {
        val currentDate = LocalDate.now()
        currentSeasonTimetable = getCurrentSeasonTimetable(currentDate)
        nextSeasonTimeTables = getNextSeasonTimetable()
    }


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

    fun getStatusColor(): StatusColor {
        val currentDate = LocalDate.now()
        val currentTime = LocalTime.now()
        if (isOpen(currentDate, currentTime)) {
            if (isClosingSoon(currentDate, currentTime)) {
                return StatusColor.YELLOW
            }
            return StatusColor.GREEN
        } else {
            return StatusColor.RED
        }
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
    private fun getCurrentSeasonTimetable(date: LocalDate): TimeTable {
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
    private fun getNextSeasonTimetable(): TimeTable {
        if (currentSeasonTimetable == summerTimeTable) {
            return winterTimetable
        }

        return summerTimeTable
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
        var output = ""
        output += "ID: $id"
        output += "$adrecaNom - ${municipality}\n"
        output += "Address $address\n"
        output += "Description: $description\n"
        output += "BibliotecaVirtualUrl $bibliotecaVirtualUrl\n"
        output += "Emails $emails\n"
        output += "Phones $phones\n"
        output += "WebUrl $webUrl\n"
        output += "Image $image\n"
        output += "WinterTimetable: ${winterTimetable}\nSummerTimetable $summerTimeTable\nNextSeasonTimetable $nextSeasonTimeTables"
        return output
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Library

        if (id != other.id) return false
        if (adrecaNom != other.adrecaNom) return false
        if (description != other.description) return false
        if (municipality != other.municipality) return false
        if (address != other.address) return false
        if (bibliotecaVirtualUrl != other.bibliotecaVirtualUrl) return false
        if (emails != other.emails) return false
        if (phones != other.phones) return false
        if (webUrl != other.webUrl) return false
        if (image != other.image) return false
        if (summerTimeTable != other.summerTimeTable) return false
        if (winterTimetable != other.winterTimetable) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + adrecaNom.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + municipality.hashCode()
        result = 31 * result + address.hashCode()
        result = 31 * result + (bibliotecaVirtualUrl?.hashCode() ?: 0)
        result = 31 * result + emails.hashCode()
        result = 31 * result + phones.hashCode()
        result = 31 * result + webUrl.hashCode()
        result = 31 * result + image.hashCode()
        result = 31 * result + summerTimeTable.hashCode()
        result = 31 * result + winterTimetable.hashCode()
        return result
    }


}

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