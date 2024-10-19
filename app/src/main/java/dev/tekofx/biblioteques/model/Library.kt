package dev.tekofx.biblioteques.model

import java.time.LocalDate
import java.time.LocalTime

data class Library(
    var punt_id: String,
    var adreca_nom: String,
    var descripcio: String,
    var municipi_nom: String,
    var imatge: String,
    var timetable_actual: Timetable,
    var timetable_estiu: Timetable,
    var timetable_hivern: Timetable
)


data class Timetable(
    var comenca: LocalDate?,
    var dilluns: List<TimeInterval>,
    var dimarts: List<TimeInterval>,
    var dimecres: List<TimeInterval>,
    var dijous: List<TimeInterval>,
    var divendres: List<TimeInterval>,
    var dissabte: List<TimeInterval>,
    var diumenge: List<TimeInterval>,

    )

data class TimeInterval(
    val startTime: LocalTime,
    val endTime: LocalTime,
    val observation: String? = null
)

