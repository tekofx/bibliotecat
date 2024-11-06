package dev.tekofx.biblioteques.repository

import android.util.Log
import dev.tekofx.biblioteques.call.BookService
import dev.tekofx.biblioteques.dto.BookResponse
import retrofit2.Call

class BookRepository(private val bookService: BookService) {
    fun findBooks(query: String): Call<BookResponse> {
        Log.d("BookRepository", "test")
        return bookService.findBooks(query)
    }

    fun getResultPage(searchArg: String, index: Int, total: Int): Call<BookResponse> {
        Log.d("BookRepository", "searchArg $searchArg index $index total $total")
        val url =
            "search~S171*cat?/X$searchArg&searchscope=171&SORT=DZ/X$searchArg&searchscope=171&SORT=DZ&extended=0&SUBKEY=$searchArg/$index%2C$total%2C$total%2CB/browse"
        return bookService.getResultPage(url)
    }

    fun getBookDetails(url: String): Call<BookResponse> {
        Log.d("BookRepository", "test")
        return bookService.getBookDetails(url)
    }
}
