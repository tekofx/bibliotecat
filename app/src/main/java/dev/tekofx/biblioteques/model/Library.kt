package dev.tekofx.biblioteques.model

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

data class Library(
    var puntId: String,
    var adrecaNom: String,
    var descripcio: String,
    var municipiNom: String,
    var bibliotecaVirtualUrl: String?,
    var emails: List<String>,
    var imatge: String,
    var weekTimetableCurrent: WeekTimetable,
    var weekTimetableEstiu: WeekTimetable,
    var weekTimetableHivern: WeekTimetable,
)

class LibraryTest(
    var puntId: String,
    var adrecaNom: String,
    var descripcio: String,
    var municipiNom: String,
    var bibliotecaVirtualUrl: String?,
    var emails: List<String>,
    var imatge: String,
    var estiuTimetable: SeasonTimetableTest,
    var hivernTimetable: SeasonTimetableTest
) {
    var currentSeasonalTimetable: SeasonTimetableTest
    var currentDayTimetable: DayTimetableTest
    var currentTimeInterval: TimeIntervalTest?
    var isOpen: Boolean
    var openUntilHour: LocalTime?
    var nextOpeningDay: String?
    var nextOpeningHour: LocalTime?

    init {
        val currentDate = LocalDate.of(2024, 10, 19)

        // Set weektimetable current
        this.currentSeasonalTimetable = hivernTimetable
        if (currentDate >= estiuTimetable.start && currentDate <= estiuTimetable.end) {
            this.currentSeasonalTimetable = estiuTimetable
        }

        // Set daytimetable current
        this.currentDayTimetable = currentSeasonalTimetable.getCurrentDayTimetable()

        // Set timeinterval current
        this.currentTimeInterval = currentSeasonalTimetable.getCurrentTimeInterval()

        if (currentTimeInterval == null) {
            println("closed")
            this.isOpen = false
            this.openUntilHour = null

            if (currentDayTimetable.getNextTimeInterval() == null) {
                println("next day")
                this.nextOpeningDay =
                    currentSeasonalTimetable.getNextDayTimeTable().dayOfWeek.toString()
                println(
                    "a ${
                        currentSeasonalTimetable.getNextDayTimeTable().getMorningTimeInterval()
                    }"
                )
                this.nextOpeningHour =
                    currentSeasonalTimetable.getNextDayTimeTable()
                        .getMorningTimeInterval()?.startTime
            } else {
                println("same day")
                this.nextOpeningDay = null
                this.nextOpeningHour = currentDayTimetable.getNextTimeInterval()!!.startTime

            }

        } else {
            println("open")
            this.isOpen = true
            this.openUntilHour = currentTimeInterval!!.endTime
            this.nextOpeningHour = null
            this.nextOpeningDay = null
        }

    }
}

data class SeasonTimetableTest(
    var start: LocalDate,
    var end: LocalDate,
    var estacio: String,
    var weekTimetables: Map<DayOfWeek, DayTimetableTest>,
    var observacions: String? = null
) {
    init {

    }

    fun getCurrentDayTimetable(): DayTimetableTest {
        val currentDate = LocalDate.of(2024, 10, 19)
        val currentDayOfWeek = currentDate.dayOfWeek

        return weekTimetables[currentDayOfWeek]!!
    }

    fun getCurrentTimeInterval(): TimeIntervalTest? {
        val currentTime = LocalTime.of(14, 30)
        val currentDayTimetable = getCurrentDayTimetable()
        val timeIntervals = currentDayTimetable.timeIntervals

        return timeIntervals.find { currentTime.isAfter(it.startTime) && currentTime.isBefore(it.endTime) }
    }

    fun getNextDayTimeTable(): DayTimetableTest {
        val currentDate = LocalDate.of(2024, 10, 19)
        val currentDayOfWeek = currentDate.dayOfWeek
        val nextDayOfWeek = currentDayOfWeek.plus(1)
        return weekTimetables[nextDayOfWeek]!!
    }


}

data class DayTimetableTest(
    var timeIntervals: List<TimeIntervalTest>,
    var dayOfWeek: DayOfWeek
) {
    fun getNextTimeInterval(): TimeIntervalTest? {
        val currentTime = LocalTime.of(14, 30)
        val nextTimeInterval = timeIntervals.find { currentTime.isBefore(it.startTime) }
        return nextTimeInterval

    }

    fun getMorningTimeInterval(): TimeIntervalTest? {
        for (timeInterval in timeIntervals) {
            if (timeInterval.dayPeriod == DayPeriod.MORNING) {
                return timeInterval
            }
        }
        return null
    }

}


data class WeekTimetable(
    var dateInterval: DateInterval,
    var estacio: String,
    var currentTimeInterval: TimeInterval?,
    var nextTimeInterval: TimeInterval?,
    var dilluns: List<TimeInterval>?,
    var dimarts: List<TimeInterval>?,
    var dimecres: List<TimeInterval>?,
    var dijous: List<TimeInterval>?,
    var divendres: List<TimeInterval>?,
    var dissabte: List<TimeInterval>?,
    var diumenge: List<TimeInterval>?,
    var observacions: String? = null
)


data class TimeInterval(
    val startTime: LocalTime? = null,
    val endTime: LocalTime? = null,
    var observation: String? = null,
    var dayOfWeek: String
)

enum class DayPeriod {
    MORNING,
    AFTERNOON
}


class TimeIntervalTest(
    val startTime: LocalTime? = null,
    val endTime: LocalTime? = null,
    var observation: String? = null,
    var dayPeriod: DayPeriod,


    ) {
    override fun toString(): String {
        return "$startTime - $endTime"
    }
}

data class DateInterval(
    val from: LocalDate,
    val to: LocalDate
)


