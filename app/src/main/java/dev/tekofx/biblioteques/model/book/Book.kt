package dev.tekofx.biblioteques.model.book

import dev.tekofx.biblioteques.model.BookResult
import dev.tekofx.biblioteques.model.StatusColor

class Book(
    val id: Int,
    val title: String,
    val author: String,
    val image: String,
    val publication: String?,
    var bookCopies: List<BookCopy>,
    val temporalUrl: String,
    var bookDetails: BookDetails? = null
) {


    constructor(bookResult: BookResult) : this(
        id = bookResult.id,
        title = bookResult.title,
        author = bookResult.author,
        image = bookResult.image,
        publication = bookResult.publication,
        bookCopies = emptyList(),
        temporalUrl = bookResult.url,
        bookDetails = null
    )

    override fun toString(): String {
        var output = "$id $author - $title"
        for (bookCopy in bookCopies) {
            output += "\n$bookCopy"
        }
        return output
    }

}

class BookDetails(
    val edition: String?,
    val description: String?,
    val synopsis: String?,
    val isbn: String?,
    val permanentUrl: String?,
    val collection: String?,
    val topic: String?,
    val authorUrl: String?,
    val bookCopiesUrl: String?,

    )

class BookCopy(
    val location: String,
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


enum class BookCopyAvailability {
    AVAILABLE,
    CAN_RESERVE,
    NOT_AVAILABLE

}