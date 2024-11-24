package dev.tekofx.biblioteques.repository

import android.util.Log
import dev.tekofx.biblioteques.call.BookService
import dev.tekofx.biblioteques.dto.BookResponse
import retrofit2.Call

class BookRepository(private val bookService: BookService) {
    fun findBooks(query: String, searchType: String): Call<BookResponse> {
        Log.d("BookRepository", "Request $query $searchType")
        return bookService.findBooks(query, searchType)
    }

    fun getSearchScope(): Call<BookResponse> {
        return bookService.getSearchScope()
    }

    fun getBookDetails(url: String): Call<BookResponse> {
        return bookService.getHtmlByUrl(url)
    }

    fun getHtmlByUrl(url: String): Call<BookResponse> {
        Log.d("BookRepository", "getHtmlByUrl $url")
        return bookService.getHtmlByUrl(url)
    }
}
