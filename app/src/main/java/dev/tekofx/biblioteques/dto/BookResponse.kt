package dev.tekofx.biblioteques.dto

import com.google.gson.annotations.SerializedName
import dev.tekofx.biblioteques.model.SearchResult
import dev.tekofx.biblioteques.model.book.Book
import dev.tekofx.biblioteques.model.book.BookCopy
import dev.tekofx.biblioteques.model.book.BookDetails

data class BookResponse(
    @SerializedName("body") var body: String = "",
    @SerializedName("books") var books: List<Book> = emptyList(),
    @SerializedName("bookCopies") var bookCopies: List<BookCopy> = arrayListOf(),
    @SerializedName("totalBooks") var totalBooks: Int = 0,
    @SerializedName("bookDetails") var bookDetails: BookDetails? = null,
    @SerializedName("searchResults") var searchResults: List<SearchResult> = emptyList(),
    @SerializedName("pages") var pages: List<String> = emptyList()
)

