package dev.tekofx.biblioteques.model

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

var firstDayOfWinter = LocalDate.of(2024, 9, 23)// Monday
var lastDayOfWinter = LocalDate.of(2025, 6, 20) // Friday
var firstDayOfSummer = LocalDate.of(2025, 6, 21) // Saturday
var lastDayOfSummer = LocalDate.of(2025, 9, 22) // Monday

var mondayWinterTimetable = DayTimeTable(
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

var tuesdayWinterTimetable = DayTimeTable(
    intervals = listOf(
        Interval(
            from = LocalTime.of(10, 0),
            to = LocalTime.of(14, 0),

            ),
    )

)

var wednesdayWinterTimetable = DayTimeTable(
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

var thursdayWinterTimetable = DayTimeTable(
    listOf(

        Interval(
            from = LocalTime.of(16, 15),
            to = LocalTime.of(21, 0),
        ),
    )

)

var fridayWinterTimetable = DayTimeTable(
    listOf(

        Interval(
            from = LocalTime.of(18, 0),
            to = LocalTime.of(20, 30),
        ),
    )
)

var saturdayWinterTimetable = DayTimeTable(
    listOf(

        Interval(
            from = LocalTime.of(9, 0),
            to = LocalTime.of(13, 30),

            ),
    )
)

var sundayWinterTimetable = DayTimeTable(
    listOf()
)


var mondaySummerTimetable = DayTimeTable(
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

var tuesdaySummerTimetable = DayTimeTable(
    intervals = listOf(
        Interval(
            from = LocalTime.of(17, 0),
            to = LocalTime.of(22, 0),

            ),
    )

)

var wednesdaySummerTimetable = DayTimeTable(
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

var thursdaySummerTimetable = DayTimeTable(
    listOf(

        Interval(
            from = LocalTime.of(15, 0),
            to = LocalTime.of(21, 40),
        ),
    )

)

var fridaySummerTimetable = DayTimeTable(
    listOf(

        Interval(
            from = LocalTime.of(14, 0),
            to = LocalTime.of(19, 30),
        ),
    )
)

var saturdaySummerTimetable = DayTimeTable(
    listOf(

        Interval(
            from = LocalTime.of(9, 0),
            to = LocalTime.of(22, 30),

            ),
    )
)

var sundaySummerTimetable = DayTimeTable(
    listOf()
)

var winterTimetable = TimeTable(
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
    )
)

var summerTimetable = TimeTable(
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
    )
)


var LibraryDummy = Library(

    puntId = "biblioteca424096",
    adrecaNom = "Biblioteca L Esqueller",
    descripcio = " Biblioteca L Esqueller. Sant Pere de Torelló",
    municipiNom = "Sant Pere de Torelló",
    fullAddress = "Josep Badrena, 10 08572 Sant Pere de Torelló",
    bibliotecaVirtualUrl = "bibliotecaVirtualUrl",
    emails = listOf("b.st.peret.e@diba.cat"),
    phones = listOf("123456789"),
    imatge = "https://bibliotecavirtual.diba.cat/documents/350986/0/P1120129.JPGfoto+portada.jpg/9ff2c56c-7424-4d95-b734-0ef67225a281?t=1364040065786",
    summerTimeTable = summerTimetable,
    winterTimetable = winterTimetable,
)