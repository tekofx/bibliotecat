package dev.tekofx.biblioteques

import dev.tekofx.biblioteques.model.DayTimeTable
import dev.tekofx.biblioteques.model.Interval
import dev.tekofx.biblioteques.model.Library
import dev.tekofx.biblioteques.model.TimeTable
import org.junit.Before
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    private lateinit var mondayTimetable: DayTimeTable
    private lateinit var tuesdayTimetable: DayTimeTable
    private lateinit var wednesdayTimetable: DayTimeTable
    private lateinit var thursdayTimetable: DayTimeTable
    private lateinit var fridayTimetable: DayTimeTable
    private lateinit var saturdayTimetable: DayTimeTable
    private lateinit var sundayTimetable: DayTimeTable
    private lateinit var winterTimetable: TimeTable
    private lateinit var summerTimetable: TimeTable
    private lateinit var libraryTest: Library

    @Before
    fun setUp() {
        mondayTimetable = DayTimeTable(
            intervals = listOf(


                Interval(
                    from = LocalTime.of(9, 30),
                    to = LocalTime.of(14, 0)
                ),

                Interval(
                    from = LocalTime.of(15, 0),
                    to = LocalTime.of(20, 0),
                ),
            )
        )

        tuesdayTimetable = DayTimeTable(
            intervals = listOf(
                Interval(
                    from = LocalTime.of(10, 0),
                    to = LocalTime.of(14, 0),

                    ),
            )

        )

        wednesdayTimetable = DayTimeTable(
            listOf(
                Interval(
                    from = LocalTime.of(11, 20),
                    to = LocalTime.of(13, 0),
                ),

                Interval(
                    from = LocalTime.of(17, 15),
                    to = LocalTime.of(20, 0),
                ),
            )

        )

        thursdayTimetable = DayTimeTable(
            listOf(

                Interval(
                    from = LocalTime.of(16, 15),
                    to = LocalTime.of(21, 0),
                ),
            )

        )

        fridayTimetable = DayTimeTable(
            listOf(

                Interval(
                    from = LocalTime.of(18, 0),
                    to = LocalTime.of(20, 30),
                ),
            )
        )

        saturdayTimetable = DayTimeTable(
            listOf(

                Interval(
                    from = LocalTime.of(9, 0),
                    to = LocalTime.of(13, 30),

                    ),
            )
        )

        sundayTimetable = DayTimeTable(
            listOf()
        )

        winterTimetable = TimeTable(
            start = LocalDate.of(2024, 9, 24),
            end = LocalDate.of(2025, 6, 20),
            dayTimetables = mapOf(
                DayOfWeek.MONDAY to mondayTimetable,
                DayOfWeek.TUESDAY to tuesdayTimetable,
                DayOfWeek.WEDNESDAY to wednesdayTimetable,
                DayOfWeek.THURSDAY to thursdayTimetable,
                DayOfWeek.FRIDAY to fridayTimetable,
                DayOfWeek.SATURDAY to saturdayTimetable,
                DayOfWeek.SUNDAY to sundayTimetable
            )
        )

        summerTimetable = TimeTable(
            start = LocalDate.of(2024, 6, 21),
            end = LocalDate.of(2024, 9, 23),
            dayTimetables = mapOf(
                DayOfWeek.MONDAY to mondayTimetable,
                DayOfWeek.TUESDAY to tuesdayTimetable,
                DayOfWeek.WEDNESDAY to wednesdayTimetable,
                DayOfWeek.THURSDAY to thursdayTimetable,
                DayOfWeek.FRIDAY to fridayTimetable,
                DayOfWeek.SATURDAY to saturdayTimetable,
                DayOfWeek.SUNDAY to sundayTimetable
            )
        )


        libraryTest = Library(

            puntId = "1",
            adrecaNom = "Biblioteca de Test",
            descripcio = "descripcio",
            municipiNom = "municipiNom",
            bibliotecaVirtualUrl = "bibliotecaVirtualUrl",
            emails = listOf("emails"),
            imatge = "imatge",
            summerTimeTable = summerTimetable,
            winterTimetable = winterTimetable,
        )
    }

    @Test
    fun getNextDayOpenTest() {
        val saturday = LocalDate.of(2024, 10, 26)
        val latestDayOfWinter = LocalDate.of(2025, 6, 20)
        
        assert(libraryTest.getNextDayOpen(saturday) == LocalDate.of(2024, 10, 28))
        assert(libraryTest.getNextDayOpen(latestDayOfWinter) == LocalDate.of(2025, 6, 21))
    }
}