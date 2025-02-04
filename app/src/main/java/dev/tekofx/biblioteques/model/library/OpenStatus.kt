package dev.tekofx.biblioteques.model.library

import dev.tekofx.biblioteques.model.StatusColor
import dev.tekofx.biblioteques.model.holiday.Holiday
import dev.tekofx.biblioteques.utils.formatDayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime


sealed class OpenStatusEnum {
    sealed class Open : OpenStatusEnum() {
        object open : Open()
        object closingSoon : Open()
    }

    sealed class Closed : OpenStatusEnum() {
        object openInAfternoon : Closed()
        object openTomorrow : Closed()
        object openInDays : Closed()
        object closedTemporarily : Closed()
        object holiday : Closed()
        object closed : Closed()
    }
}

class OpenStatus(
    val holidays: List<Holiday>,
    val timetable: Timetable
) {
    var open: OpenStatusEnum = OpenStatusEnum.Closed.closed
    var color: StatusColor = StatusColor.RED
    var message: String = ""
    private var currentDate: LocalDate = LocalDate.now()
    private var currentTime: LocalTime = LocalTime.now()

    init {
        initialize()
    }

    private fun initialize() {
        message = generateStateMessage()

        when {
            isOpen() -> {
                open = OpenStatusEnum.Open.open
                color = getStatusColor()
            }

            isClosingSoon() -> {
                open = OpenStatusEnum.Open.closingSoon
                color = StatusColor.YELLOW
            }

            opensInAfternoon() -> {
                open = OpenStatusEnum.Closed.openInAfternoon
                color = StatusColor.RED
            }

            opensTomorrow() -> {
                open = OpenStatusEnum.Closed.openTomorrow
                color = StatusColor.RED
            }

            opensInDays() -> {
                open = OpenStatusEnum.Closed.openInDays
                color = StatusColor.RED
            }

            isHoliday() -> {
                open = OpenStatusEnum.Closed.holiday
                color = StatusColor.ORANGE
            }

            else -> {
                open = OpenStatusEnum.Closed.closedTemporarily
                color = StatusColor.RED
            }
        }


    }

    fun setDateTime(date: LocalDate, time: LocalTime) {
        currentDate = date
        currentTime = time
        initialize()
    }


    private fun isOpen(): Boolean {
        holidays.find { it.date == currentDate }?.let { return false }
        val dayTimetable = timetable.getCurrentDayTimetable(currentDate)
        return dayTimetable?.timeIntervals?.any { interval ->
            currentTime.isAfter(interval.from) && currentTime.isBefore(interval.to)
        } ?: false
    }

    private fun opensInAfternoon(): Boolean {
        val nextIntervalOfDay = timetable.getNextIntervalOfDay(currentDate, currentTime)
        return nextIntervalOfDay != null
    }

    private fun opensTomorrow(): Boolean {
        val nextDay = timetable.getNextDayOpen(currentDate)
        return nextDay != null && nextDay == currentDate.plusDays(1)
    }

    private fun opensInDays(): Boolean {
        val nextDay = timetable.getNextDayOpen(currentDate)
        return nextDay != null && nextDay != currentDate.plusDays(1)
    }

    private fun isHoliday(): Boolean {
        return holidays.find { it.date == currentDate } != null
    }


    private fun getStatusColor(): StatusColor {
        holidays.find { it.date == currentDate }?.let { return StatusColor.ORANGE }
        return when {
            isOpen() -> {
                if (isClosingSoon()) {
                    StatusColor.YELLOW
                } else {
                    StatusColor.GREEN
                }
            }

            else -> StatusColor.RED
        }
    }

    private fun isClosingSoon(): Boolean {
        val currentInterval = timetable.getCurrentInterval(currentDate, currentTime) ?: return false
        return Duration.between(currentTime, currentInterval.to).toMinutes() <= 60
    }

    private fun generateStateMessage(): String {
        holidays.find { it.date == currentDate }?.let { return "Festiu ${it.holiday}" }

        if (isOpen()) {
            val currentInterval = timetable.getCurrentInterval(currentDate, currentTime)!!
            return if (isClosingSoon()) {
                "Obert · Tanca a les ${currentInterval.to}"
            } else {
                "Obert · Fins a ${currentInterval.to}"
            }
        } else {
            val nextIntervalOfDay = timetable.getNextIntervalOfDay(currentDate, currentTime)
            if (nextIntervalOfDay != null) {
                return "Tancat · Obre a las ${nextIntervalOfDay.from}"
            }

            val nextDay = timetable.getNextDayOpen(currentDate) ?: return "Tancat temporalment"
            val nextDayTimetable = timetable.getCurrentDayTimetable(nextDay)
            val nextDayName = formatDayOfWeek(nextDay.dayOfWeek)
            return if (nextDay == currentDate.plusDays(1)) {
                "Tancat · Obre demà a las ${nextDayTimetable?.timeIntervals?.firstOrNull()?.from}"
            } else {
                "Tancat · Obre el $nextDayName a las ${nextDayTimetable?.timeIntervals?.firstOrNull()?.from}"
            }
        }
    }

}