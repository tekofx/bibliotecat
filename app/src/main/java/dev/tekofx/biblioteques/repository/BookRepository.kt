package dev.tekofx.biblioteques.repository

import android.util.Log
import dev.tekofx.biblioteques.call.BookService
import dev.tekofx.biblioteques.dto.BookResponse
import retrofit2.Call

class BookRepository(private val bookService: BookService) {
    fun getBook(query: String): Call<BookResponse> {
        Log.d("BookRepository", "test")
        return bookService.getBook(query)
    }

    fun getBookDetails(url: String): Call<BookResponse> {
        Log.d("BookRepository", "test")
        return bookService.getBookDetails(url)
    }
}
