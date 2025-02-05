package dev.tekofx.biblioteques

import dev.tekofx.biblioteques.model.StatusColor
import dev.tekofx.biblioteques.model.library.LibraryStatus
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime


class TimetableTest {


    @Test
    fun isOpenTest() {
        val monday = LocalDate.of(2024, 10, 21)
        val time16 = LocalTime.of(16, 0)

        assertEquals(
            LibraryStatus(
                Library.library,
                message = "Obert · Fins a 20:30",
                color = StatusColor.GREEN
            ), timetable.getOpenStatus(monday, time16)
        )
    }

    @Test
    fun isClosingSoonTest() {
        val monday = LocalDate.of(2024, 10, 21)
        val time20 = LocalTime.of(20, 0)
        assertEquals(
            LibraryStatus(
                Library.closingSoon,
                message = "Obert · Tanca a les 20:30",
                color = StatusColor.YELLOW
            ), timetable.getOpenStatus(monday, time20)
        )
    }

    @Test
    fun openInAfternoonTest() {
        val tuesday = LocalDate.of(2024, 10, 22)
        val time14 = LocalTime.of(14, 0)
        assertEquals(
            LibraryStatus(
                Closed.libraryInAfternoon,
                message = "Tancat · Obre a las 15:30",
                color = StatusColor.RED
            ), timetable.getOpenStatus(tuesday, time14)
        )
    }

    @Test
    fun openTomorrowTest() {
        val tuesday = LocalDate.of(2024, 10, 22)
        val time21 = LocalTime.of(21, 0)
        assertEquals(
            LibraryStatus(
                Closed.libraryTomorrow,
                message = "Tancat · Obre demà a las 15:30",
                color = StatusColor.RED
            ), timetable.getOpenStatus(tuesday, time21)
        )
    }

    @Test
    fun openInDaysTest() {
        val friday = LocalDate.of(2025, 8, 23)
        val time21 = LocalTime.of(21, 0)
        assertEquals(
            LibraryStatus(
                Closed.libraryInDays,
                message = "Tancat · Obre el Dilluns a las 15:30",
                color = StatusColor.RED
            ), timetable.getOpenStatus(friday, time21)
        )
    }

    @Test
    fun closedTemporarilyTest() {
        val tuesday = LocalDate.of(2024, 9, 1)
        val time21 = LocalTime.of(10, 0)

        assertEquals(
            LibraryStatus(
                Closed.closedTemporarily,
                message = "Tancat temporalment",
                color = StatusColor.RED
            ),
            emptyTimetable.getOpenStatus(tuesday, time21)
        )

    }

    @Test
    fun holidayTest() {
        val tuesday = LocalDate.of(2024, 1, 1)
        val time21 = LocalTime.of(10, 0)
        assertEquals(
            LibraryStatus(
                Closed.holiday,
                message = "Festiu Cap d'Any",
                color = StatusColor.ORANGE
            ), timetable.getOpenStatus(tuesday, time21)
        )
    }

    @Test
    fun getSeasonTimetableOfDateTest() {
        // Winter
        assertEquals(winterTimetable, timetable.getSeasonTimetableOfDate(firstDayOfWinter))
        assertEquals(summerTimetable, timetable.getSeasonTimetableOfDate(lastDayOfWinter))

        // Summer
        assertEquals(summerTimetable, timetable.getSeasonTimetableOfDate(firstDayOfSummer))
        assertEquals(winterTimetable, timetable.getSeasonTimetableOfDate(lastDayOfSummer))
    }

    @Test
    fun getNextSeasonTimeTableOfDateTest() {
        // Winter
        assertEquals(summerTimetable, timetable.getNextSeasonTimetableOfDate(firstDayOfWinter))
        assertEquals(winterTimetable, timetable.getNextSeasonTimetableOfDate(lastDayOfWinter))

        // Summer
        assertEquals(winterTimetable, timetable.getNextSeasonTimetableOfDate(firstDayOfSummer))
        assertEquals(summerTimetable, timetable.getNextSeasonTimetableOfDate(lastDayOfSummer))
    }

}