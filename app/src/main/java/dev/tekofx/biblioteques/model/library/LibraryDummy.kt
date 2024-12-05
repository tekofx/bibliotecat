package dev.tekofx.biblioteques.model.library

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

var firstDayOfWinterDummy = LocalDate.of(2024, 9, 23)// Monday
var lastDayOfWinterDummy = LocalDate.of(2025, 6, 20) // Friday
var firstDayOfSummerDummy = LocalDate.of(2025, 6, 21) // Saturday
var lastDayOfSummerDummy = LocalDate.of(2025, 9, 22) // Monday

var mondayWinterTimetableDummy = DayTimeTable(
    timeIntervals = listOf(


        TimeInterval(
            from = LocalTime.of(9, 30),
            to = LocalTime.of(14, 0)
        ),

        TimeInterval(
            from = LocalTime.of(15, 0),
            to = LocalTime.of(20, 0),
        ),
    )
)

var tuesdayWinterTimetableDummy = DayTimeTable(
    timeIntervals = listOf(
        TimeInterval(
            from = LocalTime.of(10, 0),
            to = LocalTime.of(14, 0),

            ),
    )

)

var wednesdayWinterTimetableDummy = DayTimeTable(
    listOf(
        TimeInterval(
            from = LocalTime.of(11, 20),
            to = LocalTime.of(13, 0),
        ),

        TimeInterval(
            from = LocalTime.of(17, 15),
            to = LocalTime.of(20, 0),
        ),
    )

)

var thursdayWinterTimetableDummy = DayTimeTable(
    listOf(

        TimeInterval(
            from = LocalTime.of(16, 15),
            to = LocalTime.of(21, 0),
        ),
    )

)

var fridayWinterTimetableDummy = DayTimeTable(
    listOf(

        TimeInterval(
            from = LocalTime.of(18, 0),
            to = LocalTime.of(20, 30),
        ),
    )
)

var saturdayWinterTimetableDummy = DayTimeTable(
    listOf(

        TimeInterval(
            from = LocalTime.of(9, 0),
            to = LocalTime.of(13, 30),

            ),
    )
)

var sundayWinterTimetableDummy = DayTimeTable(
    listOf()
)


var mondaySummerTimetableDummy = DayTimeTable(
    timeIntervals = listOf(


        TimeInterval(
            from = LocalTime.of(11, 30),
            to = LocalTime.of(14, 0)
        ),

        TimeInterval(
            from = LocalTime.of(17, 0),
            to = LocalTime.of(22, 0),
        ),
    )
)

var tuesdaySummerTimetableDummy = DayTimeTable(
    timeIntervals = listOf(
        TimeInterval(
            from = LocalTime.of(17, 0),
            to = LocalTime.of(22, 0),

            ),
    )

)

var wednesdaySummerTimetableDummy = DayTimeTable(
    listOf(
        TimeInterval(
            from = LocalTime.of(9, 20),
            to = LocalTime.of(11, 0),
        ),

        TimeInterval(
            from = LocalTime.of(17, 15),
            to = LocalTime.of(18, 0),
        ),
    )

)

var thursdaySummerTimetableDummy = DayTimeTable(
    listOf(

        TimeInterval(
            from = LocalTime.of(15, 0),
            to = LocalTime.of(21, 40),
        ),
    )

)

var fridaySummerTimetableDummy = DayTimeTable(
    listOf(

        TimeInterval(
            from = LocalTime.of(14, 0),
            to = LocalTime.of(19, 30),
        ),
    )
)

var saturdaySummerTimetableDummy = DayTimeTable(
    listOf(

        TimeInterval(
            from = LocalTime.of(9, 0),
            to = LocalTime.of(22, 30),

            ),
    )
)

var sundaySummerTimetableDummy = DayTimeTable(
    listOf()
)

var winterTimetableDummy = SeasonTimeTable(
    start = firstDayOfWinterDummy,
    end = lastDayOfWinterDummy,
    dayTimetables = mapOf(
        DayOfWeek.MONDAY to mondayWinterTimetableDummy,
        DayOfWeek.TUESDAY to tuesdayWinterTimetableDummy,
        DayOfWeek.WEDNESDAY to wednesdayWinterTimetableDummy,
        DayOfWeek.THURSDAY to thursdayWinterTimetableDummy,
        DayOfWeek.FRIDAY to fridayWinterTimetableDummy,
        DayOfWeek.SATURDAY to saturdayWinterTimetableDummy,
        DayOfWeek.SUNDAY to sundayWinterTimetableDummy
    ),
    season = Season.WINTER,
    observation = ""
)

var dummySummerTimetableDummy = SeasonTimeTable(
    start = firstDayOfSummerDummy,
    end = lastDayOfSummerDummy,
    dayTimetables = mapOf(
        DayOfWeek.MONDAY to mondaySummerTimetableDummy,
        DayOfWeek.TUESDAY to tuesdaySummerTimetableDummy,
        DayOfWeek.WEDNESDAY to wednesdaySummerTimetableDummy,
        DayOfWeek.THURSDAY to thursdaySummerTimetableDummy,
        DayOfWeek.FRIDAY to fridaySummerTimetableDummy,
        DayOfWeek.SATURDAY to saturdaySummerTimetableDummy,
        DayOfWeek.SUNDAY to sundaySummerTimetableDummy
    ),
    season = Season.SUMMER,
    observation = ""
)


var LibraryDummy = Library(

    id = "biblioteca424096",
    adrecaNom = "Biblioteca L Esqueller",
    description = " Biblioteca L Esqueller. Sant Pere de Torelló",
    municipality = "Sant Pere de Torelló",
    address = "Josep Badrena, 10 08572 Sant Pere de Torelló",
    bibliotecaVirtualUrl = "bibliotecaVirtualUrl",
    emails = listOf("b.st.peret.e@diba.cat"),
    phones = listOf("123456789"),
    webUrl = "https://test.com",
    location = "1234,234",
    image = "https://bibliotecavirtual.diba.cat/documents/350986/0/P1120129.JPGfoto+portada.jpg/9ff2c56c-7424-4d95-b734-0ef67225a281?t=1364040065786",
    summerSeasonTimeTable = dummySummerTimetableDummy,
    winterTimetable = winterTimetableDummy,
)