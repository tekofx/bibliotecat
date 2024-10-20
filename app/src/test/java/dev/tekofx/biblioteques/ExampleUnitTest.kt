package dev.tekofx.biblioteques

import dev.tekofx.biblioteques.model.DayPeriod
import dev.tekofx.biblioteques.model.DayTimetableTest
import dev.tekofx.biblioteques.model.LibraryTest
import dev.tekofx.biblioteques.model.SeasonTimetableTest
import dev.tekofx.biblioteques.model.TimeIntervalTest
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

    private lateinit var weekTimetable: Map<DayOfWeek, DayTimetableTest>
    private lateinit var mondayTimetable: DayTimetableTest
    private lateinit var tuesdayTimetable: DayTimetableTest
    private lateinit var wednesdayTimetable: DayTimetableTest
    private lateinit var thursdayTimetable: DayTimetableTest
    private lateinit var fridayTimetable: DayTimetableTest
    private lateinit var saturdayTimetable: DayTimetableTest
    private lateinit var sundayTimetable: DayTimetableTest
    private lateinit var winterTimetable: SeasonTimetableTest
    private lateinit var summerTimetable: SeasonTimetableTest
    private lateinit var libraryTest: LibraryTest

    @Before
    fun setUp() {
        mondayTimetable = DayTimetableTest(
            listOf(
                TimeIntervalTest(
                    startTime = LocalTime.of(9, 30),
                    endTime = LocalTime.of(14, 0),
                    dayPeriod = DayPeriod.MORNING
                ),

                TimeIntervalTest(
                    startTime = LocalTime.of(15, 0),
                    endTime = LocalTime.of(20, 0),
                    dayPeriod = DayPeriod.AFTERNOON
                )
            ),
            DayOfWeek.MONDAY
        )

        tuesdayTimetable = DayTimetableTest(
            listOf(
                TimeIntervalTest(
                    startTime = LocalTime.of(10, 0),
                    endTime = LocalTime.of(14, 0),
                    dayPeriod = DayPeriod.MORNING

                ),

                ),
            DayOfWeek.TUESDAY
        )

        wednesdayTimetable = DayTimetableTest(
            listOf(
                TimeIntervalTest(
                    startTime = LocalTime.of(11, 20),
                    endTime = LocalTime.of(13, 0),
                    dayPeriod = DayPeriod.MORNING
                ),

                TimeIntervalTest(
                    startTime = LocalTime.of(17, 15),
                    endTime = LocalTime.of(20, 0),
                    dayPeriod = DayPeriod.AFTERNOON
                )

            ),
            DayOfWeek.WEDNESDAY
        )

        thursdayTimetable = DayTimetableTest(
            listOf(
                TimeIntervalTest(
                    startTime = LocalTime.of(16, 15),
                    endTime = LocalTime.of(21, 0),
                    dayPeriod = DayPeriod.AFTERNOON
                )

            ),
            DayOfWeek.THURSDAY
        )

        fridayTimetable = DayTimetableTest(
            listOf(
                TimeIntervalTest(
                    startTime = LocalTime.of(18, 0),
                    endTime = LocalTime.of(20, 30),
                    dayPeriod = DayPeriod.AFTERNOON
                )

            ),
            DayOfWeek.FRIDAY
        )

        saturdayTimetable = DayTimetableTest(
            listOf(
                TimeIntervalTest(
                    startTime = LocalTime.of(9, 0),
                    endTime = LocalTime.of(13, 30),
                    dayPeriod = DayPeriod.MORNING

                )

            ),
            DayOfWeek.SATURDAY
        )

        sundayTimetable = DayTimetableTest(
            listOf(),
            DayOfWeek.SUNDAY
        )

        weekTimetable = mapOf(
            DayOfWeek.MONDAY to mondayTimetable,
            DayOfWeek.TUESDAY to tuesdayTimetable,
            DayOfWeek.WEDNESDAY to wednesdayTimetable,
            DayOfWeek.THURSDAY to thursdayTimetable,
            DayOfWeek.FRIDAY to fridayTimetable,
            DayOfWeek.SATURDAY to saturdayTimetable,
            DayOfWeek.SUNDAY to sundayTimetable
        )

        winterTimetable = SeasonTimetableTest(
            start = LocalDate.of(2024, 9, 24),
            end = LocalDate.of(2025, 6, 20),
            estacio = "Hivern",
            weekTimetables = weekTimetable
        )

        summerTimetable = SeasonTimetableTest(
            start = LocalDate.of(2024, 6, 21),
            end = LocalDate.of(2024, 9, 23),
            estacio = "Estiu",
            weekTimetables = weekTimetable
        )

        libraryTest = LibraryTest(

            puntId = "1",
            adrecaNom = "Biblioteca de Test",
            descripcio = "descripcio",
            municipiNom = "municipiNom",
            bibliotecaVirtualUrl = "bibliotecaVirtualUrl",
            emails = listOf("emails"),
            imatge = "imatge",
            hivernTimetable = winterTimetable,
            estiuTimetable = summerTimetable,
        )
    }

    @Test
    fun getCurrentDayTimetable() {
        println(libraryTest.nextOpeningDay)
        println(libraryTest.nextOpeningHour)


        assert(true)


    }
}