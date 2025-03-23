package dev.tekofx.biblioteques.repository

import android.util.Log
import dev.tekofx.biblioteques.call.HolidayService
import dev.tekofx.biblioteques.call.LibraryService
import dev.tekofx.biblioteques.dto.LibraryResponse
import retrofit2.awaitResponse
import java.net.UnknownHostException

class LibraryRepository(
    private val libraryService: LibraryService,
    private val holidayService: HolidayService
) {
    suspend fun getLibraries(): LibraryResponse {
        try {
            val librariesResponse = libraryService.getLibraries().awaitResponse()
            val libraries = librariesResponse.body()?.elements.orEmpty()
            val municipalities = librariesResponse.body()?.municipalities.orEmpty()


            return LibraryResponse(libraries, municipalities)
        } catch (e: UnknownHostException) {
            Log.e("LibraryRepository", "Error getting libraries $e")
            throw e
        } catch (e: Exception) {
            Log.e("LibraryRepository", "Error getting libraries $e")
            throw e
        }
    }

}