package dev.tekofx.bibliotecat.model.holiday

import java.time.LocalDate

data class Holiday(
    val year: Int,
    val date: LocalDate,
    val place: String,
    val postalCode: String,
    val name: String
)