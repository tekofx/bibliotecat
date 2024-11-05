package dev.tekofx.biblioteques.model.book

class Book(
    val id: String,
    val title: String,
    val author: String,
    val image: String,
    val publication: String,
    var bookCopies: List<BookCopy>,
    val temporalUrl: String
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
    val notes: String
) {
    override fun toString(): String {
        return "$location $signature $status $notes"
    }
}