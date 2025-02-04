package dev.tekofx.biblioteques.model.library

import dev.tekofx.biblioteques.model.holiday.Holiday
import java.time.LocalDate
import java.time.LocalTime

class Library(
    val id: String,
    val adrecaNom: String,
    val description: String,
    val municipality: String,
    val postalCode: String,
    val address: String,
    val bibliotecaVirtualUrl: String?,
    val emails: List<String>?,
    val phones: List<String>?,
    val webUrl: String?,
    val location: List<Double>,
    var image: String,
    val timetable: Timetable,
    var holidays: List<Holiday> = emptyList<Holiday>(),
) {

    var openStatus: OpenStatus = OpenStatus(holidays, timetable)


    fun setDateTime(date: LocalDate, time: LocalTime) {
        openStatus.setDateTime(date, time)
    }


}