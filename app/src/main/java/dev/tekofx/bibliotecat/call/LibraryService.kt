package dev.tekofx.bibliotecat.call

import dev.tekofx.bibliotecat.dto.LibraryResponse
import dev.tekofx.bibliotecat.model.library.LibraryConverterFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.GET

interface LibraryService {

    @GET(value = "biblioteques/format/json/ord-municipi_nom/asc")
    fun getLibraries(): Call<LibraryResponse>


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