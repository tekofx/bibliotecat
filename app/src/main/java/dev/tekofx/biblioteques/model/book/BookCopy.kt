package dev.tekofx.biblioteques.model.book

import dev.tekofx.biblioteques.model.StatusColor

class BookCopy(
    val location: String,
    val bibliotecaVirtualUrl: String?,
    val signature: String,
    val status: String,
    val availability: BookCopyAvailability,
    val notes: String?,
    val statusColor: StatusColor
) {
    override fun toString(): String {
        return "$location $signature $status $notes"
    }
}

