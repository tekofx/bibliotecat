package dev.tekofx.biblioteques.model.book

class Book(
    val id: String,
    val title: String,
    val author: String,
    val image: String,
    val edition: String


) {


    override fun toString(): String {
        return "$id $author - $title"
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