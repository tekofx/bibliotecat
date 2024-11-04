package dev.tekofx.biblioteques.dto

import com.google.gson.annotations.SerializedName
import dev.tekofx.biblioteques.model.book.Book

data class BookResponse(
    @SerializedName("books") var books: ArrayList<Book> = arrayListOf()
)