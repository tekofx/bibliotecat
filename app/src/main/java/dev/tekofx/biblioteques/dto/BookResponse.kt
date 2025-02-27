package dev.tekofx.biblioteques.dto

import com.google.gson.annotations.SerializedName
import dev.tekofx.biblioteques.model.book.Book
import dev.tekofx.biblioteques.model.book.BookCopy
import dev.tekofx.biblioteques.model.book.BookDetails
import dev.tekofx.biblioteques.model.result.SearchResult
import dev.tekofx.biblioteques.model.result.SearchResults
import dev.tekofx.biblioteques.model.search.SearchArgument

data class BookResponse(
    @SerializedName("book") var book: Book? = null,
    @SerializedName("bookCopies") var bookCopies: List<BookCopy> = arrayListOf(),
    @SerializedName("thereAreMoreCopies") var thereAreMoreCopies: Boolean = false,
    @SerializedName("totalBooks") var totalBooks: Int = 0,
    @SerializedName("bookDetails") var bookDetails: BookDetails? = null,
    @SerializedName("pages") var pages: List<String> = emptyList(),
    @SerializedName("results") var results: SearchResults<out SearchResult>? = null,
    @SerializedName("catalogs") var catalogs: List<SearchArgument> = emptyList()
)

