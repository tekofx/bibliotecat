package dev.tekofx.biblioteques.model.library

import dev.tekofx.biblioteques.model.StatusColor
import dev.tekofx.biblioteques.model.holiday.Holiday
import dev.tekofx.biblioteques.utils.formatDayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

class Library(
    val id: String,
    val adrecaNom: String,
    val description: String,
    val municipality: String,
    val postalCode: String,
    val address: String,
    val bibliotecaVirtualUrl: String?,
    val emails: List<String>?,
    val phones: List<String>?,
    val webUrl: String?,
    val location: List<Double>,
    var image: String,
    val timetable: Timetable,
    var holidays: List<Holiday> = emptyList<Holiday>(),
    private var currentDate: LocalDate? = null
) {


    init {
        if (currentDate == null) {
            currentDate = LocalDate.now()
        }
    }

    fun setCurrentDate(date: LocalDate) {
        currentDate = date
    }

    fun isOpen(date: LocalDate, hora: LocalTime): Boolean {
        holidays.find { it.date == date }?.let { return false }
        val dayTimetable = timetable.getCurrentDayTimetable(date)
        return dayTimetable?.timeIntervals?.any { interval ->
            hora.isAfter(interval.from) && hora.isBefore(interval.to)
        } ?: false
    }

    fun getStatusColor(): StatusColor {
        val currentDate = LocalDate.now()
        val currentTime = LocalTime.now()

        holidays.find { it.date == currentDate }?.let { return StatusColor.ORANGE }
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

    fun isClosingSoon(date: LocalDate, time: LocalTime): Boolean {
        val currentInterval = timetable.getCurrentInterval(date, time) ?: return false
        return Duration.between(time, currentInterval.to).toMinutes() <= 60
    }

    fun generateStateMessage(date: LocalDate, time: LocalTime): String {
        holidays.find { it.date == date }?.let { return "Festiu ${it.holiday}" }

        if (isOpen(date, time)) {
            val currentInterval = timetable.getCurrentInterval(date, time)!!
            return if (isClosingSoon(date, time)) {
                "Obert · Tanca a les ${currentInterval.to}"
            } else {
                "Obert · Fins a ${currentInterval.to}"
            }
        } else {
            val nextIntervalOfDay = timetable.getNextIntervalOfDay(date, time)
            if (nextIntervalOfDay != null) {
                return "Tancat · Obre a las ${nextIntervalOfDay.from}"
            }

            val nextDay = timetable.getNextDayOpen(date) ?: return "Tancat temporalment"
            val nextDayTimetable = timetable.getCurrentDayTimetable(nextDay)
            val nextDayName = formatDayOfWeek(nextDay.dayOfWeek)
            return if (nextDay == date.plusDays(1)) {
                "Tancat · Obre demà a las ${nextDayTimetable?.timeIntervals?.firstOrNull()?.from}"
            } else {
                "Tancat · Obre el $nextDayName a las ${nextDayTimetable?.timeIntervals?.firstOrNull()?.from}"
            }
        }
    }
}