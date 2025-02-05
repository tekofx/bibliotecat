package dev.tekofx.biblioteques

import dev.tekofx.biblioteques.model.holiday.Holiday
import dev.tekofx.biblioteques.model.library.DayTimeTable
import dev.tekofx.biblioteques.model.library.Library
import dev.tekofx.biblioteques.model.library.Season
import dev.tekofx.biblioteques.model.library.SeasonTimeTable
import dev.tekofx.biblioteques.model.library.TimeInterval
import dev.tekofx.biblioteques.model.library.Timetable
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

/**
 * Monday, 02/09/2024
 */
var firstDayOfWinter: LocalDate = LocalDate.of(2024, 9, 2)

/**
 * Sunday, 22/06/2025
 */
var lastDayOfWinter: LocalDate = LocalDate.of(2025, 6, 22)

/**
 * Sunday, 22/06/2025
 */
var firstDayOfSummer: LocalDate = LocalDate.of(2025, 6, 22)

/**
 * Monday, 02/09/2025
 */
var lastDayOfSummer: LocalDate = LocalDate.of(2025, 9, 2)

/**
 * 15:30 - 20:30
 */
var mondayWinterTimetable = DayTimeTable(
    timeIntervals = listOf(
        TimeInterval(
            from = LocalTime.of(15, 30),
            to = LocalTime.of(20, 30)
        ),
    )
)

/**
 * 10:00 - 13:30, 15:30 - 20:30
 */
var tuesdayWinterTimetable = DayTimeTable(
    timeIntervals = listOf(
        TimeInterval(
            from = LocalTime.of(10, 0),
            to = LocalTime.of(13, 30)
        ),
        TimeInterval(
            from = LocalTime.of(15, 30),
            to = LocalTime.of(20, 30)
        ),
    )
)

/**
 * 15:30 - 20:30
 */
var wednesdayWinterTimetable = DayTimeTable(
    listOf(
        TimeInterval(
            from = LocalTime.of(15, 30),
            to = LocalTime.of(20, 30),
        )
    )
)

/**
 * 10:00 - 13:30, 15:30 - 20:30
 */
var thursdayWinterTimetable = DayTimeTable(
    listOf(

        TimeInterval(
            from = LocalTime.of(10, 0),
            to = LocalTime.of(13, 30),
        ),
        TimeInterval(
            from = LocalTime.of(15, 30),
            to = LocalTime.of(20, 30),
        )
    )

)

/**
 * 15:30 - 20:30
 */
var fridayWinterTimetable = DayTimeTable(
    listOf(

        TimeInterval(
            from = LocalTime.of(15, 30),
            to = LocalTime.of(20, 30),
        ),
    )
)

/**
 * 10:00 - 13:30
 */
var saturdayWinterTimetable = DayTimeTable(
    listOf(

        TimeInterval(
            from = LocalTime.of(10, 0),
            to = LocalTime.of(13, 30),
        )
    )
)

/**
 * Closed
 */
var sundayWinterTimetable = DayTimeTable(
    listOf()
)

/**
 * 15:30 - 20:30
 */
var mondaySummerTimetable = DayTimeTable(
    timeIntervals = listOf(


        TimeInterval(
            from = LocalTime.of(15, 30),
            to = LocalTime.of(20, 30)
        )

    )
)

/**
 * 15:30 - 20:30
 */
var tuesdaySummerTimetable = DayTimeTable(
    timeIntervals = listOf(
        TimeInterval(
            from = LocalTime.of(15, 30),
            to = LocalTime.of(20, 30),
        ),
    )
)

/**
 * 15:30 - 20:30
 */
var wednesdaySummerTimetable = DayTimeTable(
    listOf(
        TimeInterval(
            from = LocalTime.of(15, 30),
            to = LocalTime.of(20, 30),
        )
    )
)

/**
 * 10:00 - 14:00, 15:30 - 20:30
 */
var thursdaySummerTimetable = DayTimeTable(
    listOf(
        TimeInterval(
            from = LocalTime.of(10, 0),
            to = LocalTime.of(14, 0),
        ),

        TimeInterval(
            from = LocalTime.of(15, 30),
            to = LocalTime.of(20, 30),
        ),
    )
)

/**
 * 10:00 - 14:00
 */
var fridaySummerTimetable = DayTimeTable(
    listOf(

        TimeInterval(
            from = LocalTime.of(10, 0),
            to = LocalTime.of(14, 0),
        ),
    )
)

/**
 * Closed
 */
var saturdaySummerTimetable = DayTimeTable(
    listOf()
)

/**
 * Closed
 */
var sundaySummerTimetable = DayTimeTable(
    listOf()
)

var winterTimetable = SeasonTimeTable(
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
    season = Season.WINTER,
    observation = ""
)

var emptySummerTimetable = SeasonTimeTable(
    start = firstDayOfSummer,
    end = lastDayOfSummer,
    dayTimetables = mapOf(
        DayOfWeek.MONDAY to sundaySummerTimetable,
        DayOfWeek.TUESDAY to sundaySummerTimetable,
        DayOfWeek.WEDNESDAY to sundaySummerTimetable,
        DayOfWeek.THURSDAY to sundaySummerTimetable,
        DayOfWeek.FRIDAY to sundaySummerTimetable,
        DayOfWeek.SATURDAY to sundaySummerTimetable,
        DayOfWeek.SUNDAY to sundaySummerTimetable
    ),
    season = Season.SUMMER,
    observation = ""
)

var emptyWinterTimetable = SeasonTimeTable(
    start = firstDayOfWinter,
    end = lastDayOfWinter,
    dayTimetables = mapOf(
        DayOfWeek.MONDAY to sundaySummerTimetable,
        DayOfWeek.TUESDAY to sundaySummerTimetable,
        DayOfWeek.WEDNESDAY to sundaySummerTimetable,
        DayOfWeek.THURSDAY to sundaySummerTimetable,
        DayOfWeek.FRIDAY to sundaySummerTimetable,
        DayOfWeek.SATURDAY to sundaySummerTimetable,
        DayOfWeek.SUNDAY to sundaySummerTimetable
    ),
    season = Season.WINTER,
    observation = ""
)

var summerTimetable = SeasonTimeTable(
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
    season = Season.SUMMER,
    observation = ""
)

var holidays = listOf(
    Holiday(
        year = 2024,
        date = LocalDate.of(2024, 12, 25),
        place = "Catalunya",
        postalCode = "08",
        name = "Nadal"
    ),
    Holiday(
        year = 2024,
        date = LocalDate.of(2024, 12, 26),
        place = "Catalunya",
        postalCode = "08",
        name = "Sant Esteve"
    ),
    Holiday(
        year = 2024,
        date = LocalDate.of(2024, 1, 1),
        place = "Catalunya",
        postalCode = "08",
        name = "Cap d'Any"
    ),
)


var timetable = Timetable(
    summerTimetable = summerTimetable,
    winterTimetable = winterTimetable,
    holidays = holidays
)

var emptyTimetable = Timetable(
    summerTimetable = emptySummerTimetable,
    winterTimetable = emptyWinterTimetable,
    holidays = holidays
)


var LibraryAbrera = Library(
    id = "biblioteca2058919",
    name = "Biblioteca Josep Roca i Bros",
    description = " Biblioteca Josep Roca i Bros. Abrera",
    municipality = "Abrera",
    address = "Carrer Federico García Lorca, 17 08630 Abrera",
    bibliotecaVirtualUrl = "/abrera-biblioteca-josep-roca-i-bros",
    emails = listOf("b.abrera.jrb@diba.cat"),
    phones = listOf("937 700 881"),
    webUrl = "https://www.biblioabrera.cat",
    image = "https://bibliotecavirtual.diba.cat/documents/346453/0/Abrera_edifici_exterior+vitrall.jpg/b5b6ae1d-a271-422e-8fe3-bf2a993a9713?t=1327512463534",
    location = listOf(123.0, 12.2),
    postalCode = "09012",
    timetable = timetable,
)

var LibraryCanCasacuberta = Library(
    id = "biblioteca422652",
    name = "Biblioteca Can Casacuberta",
    description = "Biblioteca Can Casacuberta. Badalona",
    municipality = "Badalona",
    address = "Carrer Mossèn Anton, 40-48 08912 Badalona",
    bibliotecaVirtualUrl = "/badalona-biblioteca-can-casacuberta",
    emails = listOf("b.badalona.cc@diba.cat"),
    phones = listOf("934 643 400"),
    webUrl = "https://badabiblios.cat",
    image = "https://bibliotecavirtual.diba.cat/documents/344811/0/Biblioteca+Can+Casacuberta+-+Entrada.jpg/92bf817c-e870-4756-9677-11fd1115fdb8?t=1337090668782",
    location = listOf(41.4451890, 2.2467790),
    postalCode = "08912",
    timetable = emptyTimetable,
)
