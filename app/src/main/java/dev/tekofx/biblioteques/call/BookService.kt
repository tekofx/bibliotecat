package dev.tekofx.biblioteques.call

import dev.tekofx.biblioteques.dto.BookResponse
import dev.tekofx.biblioteques.model.book.BookConverterFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BookService {
    @GET(value = "search*cat/?searchtype=X&searchscope=171&submit=Cercar")
    fun getBook(@Query("searcharg") searchArg: String): Call<BookResponse>

    @GET(value = "{url}")
    fun getBookCopies(@Path("url") url: String): Call<BookResponse>


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