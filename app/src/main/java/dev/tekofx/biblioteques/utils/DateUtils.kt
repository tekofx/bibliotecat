package dev.tekofx.biblioteques.utils

import java.time.DayOfWeek
import java.time.format.DateTimeFormatter
import java.util.Locale

val dayFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("EEEE", Locale("ca"))

fun formatDayOfWeek(dayOfWeek: DayOfWeek): String {
    return dayFormatter.format(dayOfWeek).replaceFirstChar { it.uppercase() }
}