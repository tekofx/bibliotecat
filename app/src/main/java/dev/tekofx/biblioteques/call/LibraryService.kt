package dev.tekofx.biblioteques.call

import dev.tekofx.biblioteques.dto.LibraryResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface LibraryService {

    @GET(value = "biblioteques/format/json/")
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
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                libraryService = retrofit.create(LibraryService::class.java)
            }

            return libraryService!!
        }
    }
}