package dev.tekofx.biblioteques.call

import dev.tekofx.biblioteques.dto.LibraryResponse
import dev.tekofx.biblioteques.model.library.LibraryConverterFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path

interface LibraryService {

    @GET(value = "biblioteques/format/json/ord-municipi_nom/asc")
    fun getLibraries(): Call<LibraryResponse>

    @GET(value = "biblioteques/format/json/camp-punt_id/{id}")
    fun getLibrary(@Path("id") id: String): Call<LibraryResponse>

    companion object {

        private var libraryService: LibraryService? = null

        /**
         * Get an instance of the library service
         * (it will create one if needed)
         */
        fun getInstance(): LibraryService {

            if (libraryService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://do.diba.cat/api/dataset/")
                    .addConverterFactory(LibraryConverterFactory.create())
                    .build()
                libraryService = retrofit.create(LibraryService::class.java)
            }

            return libraryService!!
        }
    }
}