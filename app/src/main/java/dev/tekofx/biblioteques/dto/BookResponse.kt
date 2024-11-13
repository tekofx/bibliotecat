package dev.tekofx.biblioteques.dto

import com.google.gson.annotations.SerializedName
import dev.tekofx.biblioteques.model.SearchResult
import dev.tekofx.biblioteques.model.SearchResults
import dev.tekofx.biblioteques.model.book.Book

data class BookResponse(
    @SerializedName("body") var body: String = "",
    @SerializedName("book") var book: Book? = null,
    @SerializedName("results") var results: SearchResults<out SearchResult>? = null
)

