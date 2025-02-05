package dev.tekofx.biblioteques.model.library

import dev.tekofx.biblioteques.model.holiday.Holiday
import java.time.LocalDate
import java.time.LocalTime

class Timetable(
    private val winterTimetable: SeasonTimeTable,
    private val summerTimetable: SeasonTimeTable,
    var holidays: List<Holiday>,
) {

    lateinit var currentSeasonTimetable: SeasonTimeTable
    lateinit var nextSeasonTimetable: SeasonTimeTable

    init {
        initializeTimetables(LocalDate.now())
    }

    private fun initializeTimetables(currentDate: LocalDate) {
        currentSeasonTimetable = getSeasonTimetableOfDate(currentDate)
        nextSeasonTimetable = determineNextSeasonTimetable()
    }


    private fun getSeasonTimetableOfDate(date: LocalDate): SeasonTimeTable {
        return if (date.isAfter(summerTimetable.start) && date.isBefore(summerTimetable.end)) {
            summerTimetable
        } else {
            winterTimetable
        }
    }

    private fun determineNextSeasonTimetable(): SeasonTimeTable {
        return if (currentSeasonTimetable == summerTimetable) {
            winterTimetable
        } else {
            summerTimetable
        }
    }

    fun getCurrentDayTimetable(date: LocalDate): DayTimeTable? {
        val currentTimetable = getSeasonTimetableOfDate(date)
        return currentTimetable.dayTimetables[date.dayOfWeek]
    }

    fun getNextDayOpen(date: LocalDate): LocalDate? {
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

    fun getInterval(date: LocalDate, time: LocalTime): TimeInterval? {
        val currentTimetable = getSeasonTimetableOfDate(date)
        val dayTimeTable = currentTimetable.dayTimetables[date.dayOfWeek]
        return dayTimeTable?.timeIntervals?.find { interval ->
            time == interval.from || (time.isAfter(interval.from) && time.isBefore(interval.to))
        }
    }

    fun getNextIntervalOfDay(date: LocalDate, time: LocalTime): TimeInterval? {
        val dayTimeTable = getCurrentDayTimetable(date)
        return dayTimeTable?.timeIntervals?.find { interval ->
            time.isBefore(interval.from)
        }
    }
}