package dev.tekofx.biblioteques

import dev.tekofx.biblioteques.model.library.DayTimeTable
import dev.tekofx.biblioteques.model.library.Interval
import dev.tekofx.biblioteques.model.library.Library
import dev.tekofx.biblioteques.model.library.Season
import dev.tekofx.biblioteques.model.library.TimeTable
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
class LibraryTest {

    private lateinit var mondayWinterTimetable: DayTimeTable
    private lateinit var tuesdayWinterTimetable: DayTimeTable
    private lateinit var wednesdayWinterTimetable: DayTimeTable
    private lateinit var thursdayWinterTimetable: DayTimeTable
    private lateinit var fridayWinterTimetable: DayTimeTable
    private lateinit var saturdayWinterTimetable: DayTimeTable
    private lateinit var sundayWinterTimetable: DayTimeTable
    private lateinit var mondaySummerTimetable: DayTimeTable
    private lateinit var tuesdaySummerTimetable: DayTimeTable
    private lateinit var wednesdaySummerTimetable: DayTimeTable
    private lateinit var thursdaySummerTimetable: DayTimeTable
    private lateinit var fridaySummerTimetable: DayTimeTable
    private lateinit var saturdaySummerTimetable: DayTimeTable
    private lateinit var sundaySummerTimetable: DayTimeTable
    private lateinit var winterTimetable: TimeTable
    private lateinit var summerTimetable: TimeTable
    private lateinit var libraryTest: Library

    /**
     * Saturday 21/06/2025
     */
    private lateinit var firstDayOfSummer: LocalDate

    /**
     * Monday 23/09/2024
     */
    private lateinit var firstDayOfWinter: LocalDate

    /**
     * Friday = 20/06/2025
     */
    private lateinit var lastDayOfWinter: LocalDate

    /**
     * Monday = 22/09/2025
     */
    private lateinit var lastDayOfSummer: LocalDate


    @Before
    fun setUp() {

        firstDayOfWinter = LocalDate.of(2024, 9, 23)// Monday
        lastDayOfWinter = LocalDate.of(2025, 6, 20) // Friday
        firstDayOfSummer = LocalDate.of(2025, 6, 21) // Saturday
        lastDayOfSummer = LocalDate.of(2025, 9, 22) // Monday

        mondayWinterTimetable = DayTimeTable(
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

        tuesdayWinterTimetable = DayTimeTable(
            intervals = listOf(
                Interval(
                    from = LocalTime.of(10, 0),
                    to = LocalTime.of(14, 0),

                    ),
            )

        )

        wednesdayWinterTimetable = DayTimeTable(
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

        thursdayWinterTimetable = DayTimeTable(
            listOf(

                Interval(
                    from = LocalTime.of(16, 15),
                    to = LocalTime.of(21, 0),
                ),
            )

        )

        fridayWinterTimetable = DayTimeTable(
            listOf(

                Interval(
                    from = LocalTime.of(18, 0),
                    to = LocalTime.of(20, 30),
                ),
            )
        )

        saturdayWinterTimetable = DayTimeTable(
            listOf(

                Interval(
                    from = LocalTime.of(9, 0),
                    to = LocalTime.of(13, 30),

                    ),
            )
        )

        sundayWinterTimetable = DayTimeTable(
            listOf()
        )


        mondaySummerTimetable = DayTimeTable(
            intervals = listOf(


                Interval(
                    from = LocalTime.of(11, 30),
                    to = LocalTime.of(14, 0)
                ),

                Interval(
                    from = LocalTime.of(17, 0),
                    to = LocalTime.of(22, 0),
                ),
            )
        )

        tuesdaySummerTimetable = DayTimeTable(
            intervals = listOf(
                Interval(
                    from = LocalTime.of(17, 0),
                    to = LocalTime.of(22, 0),

                    ),
            )

        )

        wednesdaySummerTimetable = DayTimeTable(
            listOf(
                Interval(
                    from = LocalTime.of(9, 20),
                    to = LocalTime.of(11, 0),
                ),

                Interval(
                    from = LocalTime.of(17, 15),
                    to = LocalTime.of(18, 0),
                ),
            )

        )

        thursdaySummerTimetable = DayTimeTable(
            listOf(

                Interval(
                    from = LocalTime.of(15, 0),
                    to = LocalTime.of(21, 40),
                ),
            )

        )

        fridaySummerTimetable = DayTimeTable(
            listOf(

                Interval(
                    from = LocalTime.of(14, 0),
                    to = LocalTime.of(19, 30),
                ),
            )
        )

        saturdaySummerTimetable = DayTimeTable(
            listOf(

                Interval(
                    from = LocalTime.of(9, 0),
                    to = LocalTime.of(22, 30),

                    ),
            )
        )

        sundaySummerTimetable = DayTimeTable(
            listOf()
        )

        winterTimetable = TimeTable(
            start = firstDayOfWinter,
            end = lastDayOfWinter,
            dayTimetables = mapOf(
                DayOfWeek.MONDAY to mondayWinterTimetable,
                DayOfWeek.TUESDAY to tuesdayWinterTimetable,
                DayOfWeek.WEDNESDAY to wednesdayWinterTimetable,
                DayOfWeek.THURSDAY to thursdayWinterTimetable,
                DayOfWeek.FRIDAY to fridayWinterTimetable,
                DayOfWeek.SATURDAY to saturdayWinterTimetable,
                DayOfWeek.SUNDAY to sundayWinterTimetable
            ),
            season = Season.WINTER
        )

        summerTimetable = TimeTable(
            start = firstDayOfSummer,
            end = lastDayOfSummer,
            dayTimetables = mapOf(
                DayOfWeek.MONDAY to mondaySummerTimetable,
                DayOfWeek.TUESDAY to tuesdaySummerTimetable,
                DayOfWeek.WEDNESDAY to wednesdaySummerTimetable,
                DayOfWeek.THURSDAY to thursdaySummerTimetable,
                DayOfWeek.FRIDAY to fridaySummerTimetable,
                DayOfWeek.SATURDAY to saturdaySummerTimetable,
                DayOfWeek.SUNDAY to sundaySummerTimetable
            ),
            season = Season.SUMMER
        )


        libraryTest = Library(

            id = "1",
            adrecaNom = "Biblioteca de Test",
            description = "descripcio",
            municipality = "municipiNom",
            address = "Avinguda Josep Tarradellas, 08901 Hospitalet de Llobregat, L'",
            bibliotecaVirtualUrl = "bibliotecaVirtualUrl",
            emails = listOf("emails"),
            phones = listOf("123456789"),
            image = "imatge",
            summerTimeTable = summerTimetable,
            winterTimetable = winterTimetable,
            webUrl = ""
        )
    }

    @Test
    fun isOpenTest() {
        val monday = LocalDate.of(2024, 10, 21)
        val sunday = LocalDate.of(2024, 10, 27)
        val time = LocalTime.of(10, 0)
        val time14 = LocalTime.of(14, 0)
        assert(libraryTest.isOpen(monday, time))
        assert(!libraryTest.isOpen(monday, time14))
        assert(!libraryTest.isOpen(sunday, time))

    }

    @Test
    fun isClosingSoonTest() {
        val monday = LocalDate.of(2024, 10, 21)
        val time = LocalTime.of(10, 0)
        val time13 = LocalTime.of(13, 0)
        assert(!libraryTest.isClosingSoon(monday, time))
        assert(libraryTest.isClosingSoon(monday, time13))
    }

    @Test
    fun getNextDayOpenTest() {
        val saturday = LocalDate.of(2024, 10, 26)

        assert(libraryTest.getNextDayOpen(saturday) == LocalDate.of(2024, 10, 28))
        assert(libraryTest.getNextDayOpen(lastDayOfWinter) == LocalDate.of(2025, 6, 21))
    }

    @Test
    fun generateStateMessageTest() {
        val monday = LocalDate.of(2024, 10, 21)
        val saturday = LocalDate.of(2024, 10, 26)
        val time10 = LocalTime.of(10, 0)
        val time14 = LocalTime.of(14, 30)
        val time16 = LocalTime.of(16, 30)
        val time21 = LocalTime.of(21, 0)

        // Check all timetables of day
        assert(libraryTest.generateStateMessage(monday, time10) == "Obert · Fins a 14:00")
        assert(libraryTest.generateStateMessage(monday, time14) == "Tancat · Obre a las 15:00")
        assert(libraryTest.generateStateMessage(monday, time16) == "Obert · Fins a 20:00")
        assert(
            libraryTest.generateStateMessage(
                monday,
                time21
            ) == "Tancat · Obre demà a las 10:00"
        )

        // Check next day closed
        assert(
            libraryTest.generateStateMessage(
                saturday,
                time21
            ) == "Tancat · Obre el Dilluns a las 09:30"
        )
    }

    @Test
    fun getCurrentSeasonTimetableTest() {
        val day = LocalDate.of(2024, 10, 21)
        libraryTest.setCurrentDate(day)
        assert(libraryTest.currentSeasonTimetable == winterTimetable)
        libraryTest.setCurrentDate(lastDayOfWinter)
        assert(libraryTest.currentSeasonTimetable == winterTimetable)
        libraryTest.setCurrentDate(lastDayOfSummer)
        assert(libraryTest.currentSeasonTimetable == summerTimetable)
        libraryTest.setCurrentDate(firstDayOfSummer)
        assert(libraryTest.currentSeasonTimetable == summerTimetable)
        libraryTest.setCurrentDate(firstDayOfWinter)
        assert(libraryTest.currentSeasonTimetable == winterTimetable)
    }


    @Test
    fun getCurrentDayTimetableTest() {
        val monday = LocalDate.of(2024, 10, 21)
        val sunday = LocalDate.of(2024, 10, 27)
        assert(libraryTest.getCurrentDayTimetable(monday) == mondayWinterTimetable)
        assert(libraryTest.getCurrentDayTimetable(sunday) == sundayWinterTimetable)

    }

    @Test
    fun getCurrentIntervalTest() {
        val monday = LocalDate.of(2024, 10, 21)
        val time = LocalTime.of(10, 0)
        val time17 = LocalTime.of(17, 0)

        assert(
            libraryTest.getCurrentInterval(monday, time) == Interval(
                from = LocalTime.of(9, 30),
                to = LocalTime.of(14, 0)
            )
        )
        assert(
            libraryTest.getCurrentInterval(lastDayOfSummer, time17) == Interval(
                from = LocalTime.of(17, 0),
                to = LocalTime.of(22, 0),
            )
        )
    }

    @Test
    fun getNextIntervalOfDayTest() {
        val monday = LocalDate.of(2024, 10, 21)
        val time = LocalTime.of(10, 0)

        assert(
            libraryTest.getNextIntervalOfDay(monday, time) == Interval(
                from = LocalTime.of(15, 0),
                to = LocalTime.of(20, 0),
            )
        )
    }
}