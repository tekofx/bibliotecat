package dev.tekofx.biblioteques.model

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
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


data class DateInterval(
    val from: LocalDate,
    val to: LocalDate
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
    var isOpen: Boolean = false
    var openUntilHour: LocalTime? = null
    var nextOpeningDay: String? = null
    var nextOpeningHour: LocalTime? = null

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


    }

    /**
     * @return the next DateTime where the library is open
     */
    fun getNextOpening(): LocalDateTime {
        // TODO: Implement
        val currentDate = LocalDate.of(2024, 10, 19)
        val dayOfWeek = currentDate.dayOfWeek

        if (currentTimeInterval == null) {
            println("closed")
            this.isOpen = false
            this.openUntilHour = null

            if (currentDayTimetable.getNextTimeInterval() == null) {
                println("next day")
                var nextDay = currentDate.plusDays(1)
                // Get day timetable for next day, checking if it's from actual season or not
                var seasonalTimetable = getSeasonalTimetableOfDate(nextDay)
                var dayTimetable = seasonalTimetable.weekTimetables[nextDay.dayOfWeek]!!

                while (dayTimetable.isClosed) {
                    nextDay = nextDay.plusDays(1)
                    seasonalTimetable = getSeasonalTimetableOfDate(nextDay)
                    dayTimetable = seasonalTimetable.weekTimetables[nextDay.dayOfWeek]!!
                }


                this.nextOpeningDay = nextDay.toString()
                this.nextOpeningHour = dayTimetable.getNextTimeInterval()!!.startTime

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


        return LocalDateTime.now()
    }

    fun getSeasonalTimetableOfDate(date: LocalDate): SeasonTimetableTest {
        if (date >= estiuTimetable.start && date <= estiuTimetable.end) {
            return estiuTimetable
        }
        return hivernTimetable
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
        val currentDayTimetable = getCurrentDayTimetable()
        return currentDayTimetable.getCurrentTimeInterval()
    }


}

data class DayTimetableTest(
    var morningTimeInterval: TimeIntervalTest,
    var afternoonTimeInterval: TimeIntervalTest,
    var dayOfWeek: DayOfWeek
) {

    var isClosed: Boolean

    init {
        if (morningTimeInterval.isClosed || afternoonTimeInterval.isClosed) {
            isClosed = true
        } else {
            isClosed = false
        }

    }


    fun getNextTimeInterval(): TimeIntervalTest? {
        val currentTime = LocalTime.of(14, 30)

        if (currentTime.isBefore(morningTimeInterval.startTime)) {
            return morningTimeInterval

        } else if (currentTime.isAfter(morningTimeInterval.startTime) && currentTime.isBefore(
                afternoonTimeInterval.startTime
            )
        ) {
            return afternoonTimeInterval
        } else if (currentTime.isAfter(afternoonTimeInterval.startTime)) {
            return null
        }

        return null
    }

    fun getCurrentTimeInterval(): TimeIntervalTest? {
        val currentTime = LocalTime.of(14, 30)
        if (currentTime.isAfter(morningTimeInterval.startTime) && currentTime.isBefore(
                morningTimeInterval.endTime
            )
        ) {
            return morningTimeInterval
        } else if (currentTime.isAfter(afternoonTimeInterval.startTime) && currentTime.isBefore(
                afternoonTimeInterval.endTime
            )
        ) {
            return afternoonTimeInterval
        }

        return null
    }

}

class TimeIntervalTest(
    val startTime: LocalTime? = null,
    val endTime: LocalTime? = null,
    var observation: String? = null,

    ) {

    var isClosed: Boolean

    init {
        if (startTime == null || endTime == null) {
            isClosed = true
        } else {
            isClosed = false
        }
    }

    override fun toString(): String {
        return "$startTime - $endTime"
    }
}