package dev.tekofx.biblioteques.model.library

import dev.tekofx.biblioteques.model.HolidayDay
import dev.tekofx.biblioteques.model.StatusColor
import dev.tekofx.biblioteques.utils.formatDayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime


enum class Season {
    WINTER,
    SUMMER
}

val seasonTranslation = mapOf(
    Season.WINTER to "Hivern",
    Season.SUMMER to "Estiu"
)


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
 * @property summerSeasonTimeTable The timetable for the summer period.
 * @property winterTimetable The timetable for the winter period.
 */
class Library(
    val id: String,
    val adrecaNom: String,
    val description: String,
    val municipality: String,
    val postalCode: String,
    val address: String,
    val bibliotecaVirtualUrl: String?,
    val emails: List<String>,
    val phones: List<String>,
    val location: String,
    val webUrl: String,
    var image: String,
    val summerSeasonTimeTable: SeasonTimeTable,
    val winterTimetable: SeasonTimeTable,
    var holidays: List<HolidayDay> = emptyList<HolidayDay>(),
    private var currentDate: LocalDate? = null
) {

    var currentSeasonTimetable: SeasonTimeTable
    var nextSeasonSeasonTimeTables: SeasonTimeTable

    init {
        if (currentDate == null) {
            currentDate = LocalDate.now()

        }
        currentSeasonTimetable = getCurrentSeasonTimetable(currentDate!!)
        nextSeasonSeasonTimeTables = getNextSeasonTimetable()
    }

    private fun initilializeLibrary() {
        currentSeasonTimetable = getCurrentSeasonTimetable(currentDate!!)
        nextSeasonSeasonTimeTables = getNextSeasonTimetable()
    }

    fun setCurrentDate(date: LocalDate) {
        currentDate = date
        initilializeLibrary()
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
        return dayTimetable?.timeIntervals?.any { interval ->
            if (interval.from != null && interval.to != null) {
                hora.isAfter(interval.from) && hora.isBefore(interval.to)
            } else {
                false
            }
        } ?: false
    }

    fun getStatusColor(): StatusColor {
        val currentDate = LocalDate.now()
        val currentTime = LocalTime.now()

        this.holidays.find { it.date == currentDate }
            ?.let { return StatusColor.ORANGE }
        return when {
            isOpen(currentDate, currentTime) -> {
                if (isClosingSoon(currentDate, currentTime)) {
                    StatusColor.YELLOW
                } else {
                    StatusColor.GREEN
                }
            }

            else -> StatusColor.RED
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
    private fun getCurrentSeasonTimetable(date: LocalDate): SeasonTimeTable {
        return if (date == summerSeasonTimeTable.start || date == summerSeasonTimeTable.end) {
            summerSeasonTimeTable
        } else if (date.isAfter(summerSeasonTimeTable.start) && date.isBefore(summerSeasonTimeTable.end)) {
            return summerSeasonTimeTable
        } else {
            winterTimetable
        }
    }

    /**
     * Gets the next season timetable based on the given date.
     *
     * @return The next season timetable.
     */
    private fun getNextSeasonTimetable(): SeasonTimeTable {
        if (currentSeasonTimetable == summerSeasonTimeTable) {
            return winterTimetable
        }

        return summerSeasonTimeTable
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

        if (!summerSeasonTimeTable.open && !winterTimetable.open) {
            return null
        }

        while (currentTimetable.dayTimetables[nextDay.dayOfWeek]?.timeIntervals.isNullOrEmpty()) {
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
    fun getCurrentInterval(date: LocalDate, time: LocalTime): TimeInterval? {
        val currentTimetable = getCurrentSeasonTimetable(date)
        val dayTimeTable = currentTimetable.dayTimetables[date.dayOfWeek]
        return dayTimeTable?.timeIntervals?.find { interval ->
            time == interval.from || (time.isAfter(interval.from) && time.isBefore(interval.to))
        }
    }

    /**
     * Gets the next interval of day based on the given date and time.
     * @param date The date to check.
     * @param time The time to check.
     * @return The next interval of day, or null if it doesn't exist.
     */
    fun getNextIntervalOfDay(date: LocalDate, time: LocalTime): TimeInterval? {
        val dayTimeTable = getCurrentDayTimetable(date)
        val nextInterval = dayTimeTable?.timeIntervals?.find { interval ->
            if (interval.from == null) {
                return null
            }
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

        val holiday = this.holidays.find {
            it.date == date
        }

        if (holiday != null) {
            return "Festiu ${holiday.holiday}"
        }

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
                return "Tancat · Obre demà a las ${nextDayTimetable?.timeIntervals?.firstOrNull()?.from}"

            }
            return "Tancat · Obre el $nextDayName a las ${nextDayTimetable?.timeIntervals?.firstOrNull()?.from}"
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
        output += "WinterTimetable: ${winterTimetable}\nSummerTimetable $summerSeasonTimeTable\nNextSeasonTimetable $nextSeasonSeasonTimeTables"
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
        if (summerSeasonTimeTable != other.summerSeasonTimeTable) return false
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
        result = 31 * result + summerSeasonTimeTable.hashCode()
        result = 31 * result + winterTimetable.hashCode()
        return result
    }


}

