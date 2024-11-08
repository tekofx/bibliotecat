package dev.tekofx.biblioteques.call

import dev.tekofx.biblioteques.dto.BookResponse
import dev.tekofx.biblioteques.model.book.BookConverterFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url


interface BookService {
    @GET(value = "search*cat/?searchscope=171&submit=Cercar")
    fun findBooks(
        @Query("searcharg") searchArg: String,
        @Query("searchtype") searchType: String
    ): Call<BookResponse>


    @GET
    fun getResultPage(
        @Url url: String
    ): Call<BookResponse>

    @GET(value = "{url}")
    fun getHtmlByUrl(@Path("url") url: String): Call<BookResponse>


    companion object {

        private var bookService: BookService? = null

        /**
         * Get an instance of the book service
         * (it will create one if needed)
         */
        fun getInstance(): BookService {

            if (bookService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://aladi.diba.cat/")
                    .addConverterFactory(BookConverterFactory.create())
                    .build()
                bookService = retrofit.create(BookService::class.java)
            }

            return bookService!!
        }
    }
}