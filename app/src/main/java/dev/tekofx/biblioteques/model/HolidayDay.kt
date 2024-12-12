package dev.tekofx.biblioteques.model

import java.time.LocalDate

data class HolidayDay(
    val year: Int,
    val date: LocalDate,
    val place: String,
    val postalCode: String,
    val holiday: String
)