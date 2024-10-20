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

class Test(private val puntId: String) {
    private lateinit var adrecaNom: String
    private lateinit var descripcio: String
    private lateinit var municipiNom: String
    private lateinit var imatge: String
    private lateinit var timetableActual: Timetable
    private lateinit var timetableEstiu: Timetable
    private lateinit var timetableHivern: Timetable
    private lateinit var timeIntervalActual: TimeInterval

    constructor(
        puntId: String,
        adrecaNom: String,
        descripcio: String,
        municipiNom: String,
        imatge: String,
        timetableHivern: Timetable,
        timetableEstiu: Timetable
    ) : this(puntId) {
        this.adrecaNom = adrecaNom
        this.descripcio = descripcio
        this.municipiNom = municipiNom
        this.imatge = imatge
        this.timetableHivern = timetableHivern
        this.timetableEstiu = timetableEstiu

        // Get timetable actual

    }

    fun getPuntId(): String {
        return puntId
    }

}


data class Timetable(
    var comenca: LocalDate?,
    var estacio: String? = null,
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
