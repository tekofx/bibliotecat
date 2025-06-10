package dev.tekofx.bibliotecat.repository

import android.util.Log
import dev.tekofx.bibliotecat.call.BookService
import dev.tekofx.bibliotecat.dto.BookResponse
import dev.tekofx.bibliotecat.model.search.Search
import retrofit2.Call

class BookRepository(private val bookService: BookService) {
    fun search(search: Search): Call<BookResponse> {
        Log.d("BookRepository", "Request $search")
        return bookService.search(search.query, search.searchType.value, search.catalog.value)
    }

    fun getCatalogs(): Call<BookResponse> {
        return bookService.getCatalogs()
    }

    fun getBookDetails(url: String): Call<BookResponse> {
        return bookService.getHtmlByUrl(url)
    }

    fun getHtmlByUrl(url: String): Call<BookResponse> {
        Log.d("BookRepository", "getHtmlByUrl $url")
        return bookService.getHtmlByUrl(url)
    }
}
