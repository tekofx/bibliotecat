package dev.tekofx.bibliotecat.model.book

class BookCopy(
    val location: String,
    val bibliotecaVirtualUrl: String?,
    val signature: String,
    val availability: Availability,
    val notes: String?,
) {
    override fun toString(): String {
        return "$location $signature $availability $notes"
    }
}

