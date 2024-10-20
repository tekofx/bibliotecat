package dev.tekofx.biblioteques.model

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
    var timetableActual: Timetable,
    var timetableEstiu: Timetable,
    var timetableHivern: Timetable,
)


data class Timetable(
    var dateInterval: DateInterval,
    var estacio: String? = null,
    var actualDateInterval: List<TimeInterval>,
    var dilluns: List<TimeInterval>,
    var dimarts: List<TimeInterval>,
    var dimecres: List<TimeInterval>,
    var dijous: List<TimeInterval>,
    var divendres: List<TimeInterval>,
    var dissabte: List<TimeInterval>,
    var diumenge: List<TimeInterval>,
    var observacions: String? = null

)

data class TimeInterval(
    val startTime: LocalTime? = null,
    val endTime: LocalTime? = null,
    var observation: String? = null
)

data class DateInterval(
    val from: LocalDate,
    val to: LocalDate
)


