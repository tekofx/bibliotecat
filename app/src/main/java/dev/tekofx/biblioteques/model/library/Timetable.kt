package dev.tekofx.biblioteques.model.library

import dev.tekofx.biblioteques.model.StatusColor
import dev.tekofx.biblioteques.model.holiday.Holiday
import dev.tekofx.biblioteques.utils.formatDayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

class Timetable(
    private val winterTimetable: SeasonTimeTable,
    private val summerTimetable: SeasonTimeTable,
    var holidays: List<Holiday>,
) {


    /**
     * Creates a [LibraryStatus] based on the date, time and the timetable info
     * @param date
     * @param time
     *
     * @return [LibraryStatus]
     */
    fun getOpenStatus(
        date: LocalDate, time: LocalTime
    ): LibraryStatus {
        holidays.find { it.date == date }?.let {
            return LibraryStatus(
                LibraryStatus.Value.Closed.Holiday, StatusColor.ORANGE, "Festiu ${it.name}"
            )
        }

        if (isOpen(date, time)) {
            val currentInterval = getInterval(date, time)!!
            return if (isClosingSoon(date, time)) {
                LibraryStatus(
                    LibraryStatus.Value.Open.closingSoon,
                    StatusColor.YELLOW,
                    "Obert · Tanca a les ${currentInterval.to}"
                )
            } else {
                LibraryStatus(
                    LibraryStatus.Value.Open.open,
                    StatusColor.GREEN,
                    "Obert · Fins a ${currentInterval.to}"
                )
            }
        } else {
            val nextIntervalOfDay = getNextIntervalOfDay(date, time)
            if (nextIntervalOfDay != null) {
                return LibraryStatus(
                    LibraryStatus.Value.Closed.openAfternoon,
                    StatusColor.RED,
                    "Tancat · Obre a las ${nextIntervalOfDay.from}"
                )
            }

            val nextDay = getNextDayOpen(date) ?: return LibraryStatus(
                LibraryStatus.Value.Closed.closedTemporarily, StatusColor.RED, "Tancat temporalment"
            )

            val nextDayTimetable = getDayTimetable(nextDay)
            val nextDayName = formatDayOfWeek(nextDay.dayOfWeek)
            return if (nextDay == date.plusDays(1)) {
                LibraryStatus(
                    LibraryStatus.Value.Closed.openTomorrow,
                    StatusColor.RED,
                    "Tancat · Obre demà a las ${nextDayTimetable?.timeIntervals?.firstOrNull()?.from}"
                )
            } else {
                LibraryStatus(
                    LibraryStatus.Value.Closed.openInDays,
                    StatusColor.RED,
                    "Tancat · Obre el $nextDayName a las ${nextDayTimetable?.timeIntervals?.firstOrNull()?.from}"
                )
            }
        }
    }

    /**
     * Checks if its open given a [LocalDate] and a [LocalTime]
     * @param date
     * @param time
     */
    private fun isOpen(date: LocalDate, time: LocalTime): Boolean {
        holidays.find { it.date == date }?.let { return false }
        val dayTimetable = getDayTimetable(date)
        return dayTimetable?.timeIntervals?.any { interval ->
            time.isAfter(interval.from) && time.isBefore(interval.to)
        } ?: false
    }

    /**
     * Checks if it is closing in less than an hour given a [LocalDate] and a [LocalTime]
     * @param date
     * @param time
     */
    private fun isClosingSoon(date: LocalDate, time: LocalTime): Boolean {
        val currentInterval = getInterval(date, time) ?: return false
        return Duration.between(time, currentInterval.to).toMinutes() <= 60
    }

    /**
     * Return the [SeasonTimeTable] of a given [LocalDate]
     * @param date
     *
     * @return [SeasonTimeTable]
     */
    fun getSeasonTimetableOfDate(date: LocalDate): SeasonTimeTable {
        return if ((date.isAfter(summerTimetable.start) || date.isEqual(summerTimetable.start)) && date.isBefore(
                summerTimetable.end
            )
        ) {
            summerTimetable
        } else {
            winterTimetable
        }
    }

    /**
     * Return the next [SeasonTimeTable] of a given [LocalDate]
     * @param date
     *
     * @return [SeasonTimeTable]
     */
    fun getNextSeasonTimetableOfDate(date: LocalDate): SeasonTimeTable {
        return if (getSeasonTimetableOfDate(date) == summerTimetable) {
            winterTimetable
        } else {
            summerTimetable
        }
    }

    /**
     * Return the [DayTimeTable] of a given [LocalDate]
     * @param date
     *
     * @return [DayTimeTable] if the [date] has one, null if not
     */
    private fun getDayTimetable(date: LocalDate): DayTimeTable? {
        val currentTimetable = getSeasonTimetableOfDate(date)
        return currentTimetable.dayTimetables[date.dayOfWeek]
    }

    /**
     * Return the next [LocalDate] that it's open of a given [LocalDate]
     * @param date
     *
     * @return [LocalDate] of the next day that it's open. If the [summerTimetable] and [winterTimetable] don't have any [DayTimeTable], it returns null
     */
    private fun getNextDayOpen(date: LocalDate): LocalDate? {
        var nextDay = date.plusDays(1)
        var currentTimetable = getSeasonTimetableOfDate(nextDay)

        if (!summerTimetable.open && !winterTimetable.open) {
            return null
        }
        while (currentTimetable.dayTimetables[nextDay.dayOfWeek]?.timeIntervals.isNullOrEmpty()) {
            nextDay = nextDay.plusDays(1)
            currentTimetable = getSeasonTimetableOfDate(nextDay)
        }

        return nextDay
    }

    /**
     * Return the [TimeInterval] of a given [LocalDate] and [LocalTime]
     * @param date
     * @param time
     *
     * @return [TimeInterval] of the [date] and [time]. Null if it does not have one
     */
    private fun getInterval(date: LocalDate, time: LocalTime): TimeInterval? {
        val currentTimetable = getSeasonTimetableOfDate(date)
        val dayTimeTable = currentTimetable.dayTimetables[date.dayOfWeek]
        return dayTimeTable?.timeIntervals?.find { interval ->
            time == interval.from || (time.isAfter(interval.from) && time.isBefore(interval.to))
        }
    }

    /**
     * Return the next [TimeInterval] of a given [LocalDate] and [LocalTime]
     * @param date
     * @param time
     *
     * @return next [TimeInterval] of the [date] and [time]. Null if it does not have one
     */
    private fun getNextIntervalOfDay(date: LocalDate, time: LocalTime): TimeInterval? {
        val dayTimeTable = getDayTimetable(date)
        return dayTimeTable?.timeIntervals?.find { interval ->
            time.isBefore(interval.from)
        }
    }
}