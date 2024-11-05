package dev.tekofx.biblioteques.model.book

import dev.tekofx.biblioteques.model.StatusColor

class Book(
    val id: Int,
    val title: String,
    val author: String,
    val image: String,
    val publication: String,
    var bookCopies: List<BookCopy>,
    val temporalUrl: String,
    val edition: String? = null,
    val description: String? = null,
    val synopsis: String? = null,
    val isbn: String? = null,
    val permanentUrl: String? = null
) {
    override fun toString(): String {
        var output = "$id $author - $title"
        for (bookCopy in bookCopies) {
            output += "\n$bookCopy"
        }
        return output
    }
}

class BookCopy(
    val location: String,
    val signature: String,
    val status: String,
    val notes: String?,
    val statusColor: StatusColor = StatusColor.RED
) {
    override fun toString(): String {
        return "$location $signature $status $notes"
    }
}

