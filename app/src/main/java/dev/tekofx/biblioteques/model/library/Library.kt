package dev.tekofx.biblioteques.model.library

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
) {

    var openStatus: OpenStatus = timetable.getOpenStatus(LocalDate.now(), LocalTime.now())


    fun setDateTime(date: LocalDate, time: LocalTime) {
        openStatus = timetable.getOpenStatus(date, time)
    }


}