package dev.tekofx.biblioteques.utils

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

val dayFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("EEEE", Locale("ca"))
val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale("ca"))
fun formatDayOfWeek(dayOfWeek: DayOfWeek): String {
    return dayFormatter.format(dayOfWeek).replaceFirstChar { it.uppercase() }
}

fun formatDate(localDate: LocalDate): String {
    return dateFormatter.format(localDate)
}