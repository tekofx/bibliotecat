package dev.tekofx.biblioteques.model.book

import dev.tekofx.biblioteques.model.BookResult

class Book(
    val id: Int,
    val title: String,
    val author: String,
    val image: String,
    val publication: String?,
    var bookCopies: List<BookCopy>,
    val url: String,
    var bookDetails: BookDetails? = null
) {
    constructor(bookResult: BookResult) : this(
        id = bookResult.id,
        title = bookResult.title,
        author = bookResult.author,
        image = bookResult.image,
        publication = bookResult.publication,
        bookCopies = emptyList(),
        url = bookResult.url,
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



