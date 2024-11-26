package dev.tekofx.biblioteques.dto

import com.google.gson.annotations.SerializedName
import dev.tekofx.biblioteques.model.SearchResult
import dev.tekofx.biblioteques.model.SearchResults
import dev.tekofx.biblioteques.model.book.Book
import dev.tekofx.biblioteques.model.book.BookCopy
import dev.tekofx.biblioteques.model.book.BookDetails
import dev.tekofx.biblioteques.ui.components.input.SelectItem

data class BookResponse(
    @SerializedName("body") var body: String = "",
    @SerializedName("book") var book: Book? = null,
    @SerializedName("bookCopies") var bookCopies: List<BookCopy> = arrayListOf(),
    @SerializedName("totalBooks") var totalBooks: Int = 0,
    @SerializedName("bookDetails") var bookDetails: BookDetails? = null,
    @SerializedName("pages") var pages: List<String> = emptyList(),
    @SerializedName("results") var results: SearchResults<out SearchResult>? = null,
    @SerializedName("searchScopes") var searchScopes: List<SelectItem> = emptyList()
)

