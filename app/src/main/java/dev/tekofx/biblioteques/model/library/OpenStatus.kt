package dev.tekofx.biblioteques.model.library

import dev.tekofx.biblioteques.model.StatusColor
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
    val timetable: Timetable
) {
    var status: OpenStatusEnum = OpenStatusEnum.Closed.closed
    var color: StatusColor = StatusColor.RED
    var message: String = ""
    private var currentDate: LocalDate = LocalDate.now()
    private var currentTime: LocalTime = LocalTime.now()

    init {
        initialize()
    }

    private fun initialize() {
        val data = getData()
        status = data.first
        color = data.second
        message = data.third
    }

    fun setDateTime(date: LocalDate, time: LocalTime) {
        currentDate = date
        currentTime = time
        initialize()
    }

    private fun getData(): Triple<OpenStatusEnum, StatusColor, String> {
        timetable.holidays.find { it.date == currentDate }?.let {
            Triple(OpenStatusEnum.Closed.holiday, StatusColor.ORANGE, "Festiu ${it.holiday}")
        }

        if (isOpen()) {
            val currentInterval = timetable.getInterval(currentDate, currentTime)!!
            return if (isClosingSoon()) {
                Triple(
                    OpenStatusEnum.Open.closingSoon,
                    StatusColor.YELLOW,
                    "Obert · Tanca a les ${currentInterval.to}"
                )
            } else {
                Triple(
                    OpenStatusEnum.Open.open,
                    StatusColor.GREEN,
                    "Obert · Fins a ${currentInterval.to}"
                )
            }
        } else {
            val nextIntervalOfDay = timetable.getNextIntervalOfDay(currentDate, currentTime)
            if (nextIntervalOfDay != null) {
                return Triple(
                    OpenStatusEnum.Closed.openInAfternoon,
                    StatusColor.RED,
                    "Tancat · Obre a las ${nextIntervalOfDay.from}"
                )
            }

            val nextDay = timetable.getNextDayOpen(currentDate) ?: return Triple(
                OpenStatusEnum.Closed.closedTemporarily,
                StatusColor.RED,
                "Tancat temporalment"
            )
            val nextDayTimetable = timetable.getCurrentDayTimetable(nextDay)
            val nextDayName = formatDayOfWeek(nextDay.dayOfWeek)
            return if (nextDay == currentDate.plusDays(1)) {
                Triple(
                    OpenStatusEnum.Closed.openTomorrow,
                    StatusColor.RED,
                    "Tancat · Obre demà a las ${nextDayTimetable?.timeIntervals?.firstOrNull()?.from}"
                )
            } else {
                Triple(
                    OpenStatusEnum.Closed.openInDays,
                    StatusColor.RED,
                    "Tancat · Obre el $nextDayName a las ${nextDayTimetable?.timeIntervals?.firstOrNull()?.from}"
                )
            }
        }
    }


    private fun isOpen(): Boolean {
        timetable.holidays.find { it.date == currentDate }?.let { return false }
        val dayTimetable = timetable.getCurrentDayTimetable(currentDate)
        return dayTimetable?.timeIntervals?.any { interval ->
            currentTime.isAfter(interval.from) && currentTime.isBefore(interval.to)
        } ?: false
    }

    private fun isClosingSoon(): Boolean {
        val currentInterval = timetable.getInterval(currentDate, currentTime) ?: return false
        return Duration.between(currentTime, currentInterval.to).toMinutes() <= 60
    }


}