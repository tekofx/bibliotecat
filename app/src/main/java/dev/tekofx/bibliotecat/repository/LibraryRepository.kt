package dev.tekofx.bibliotecat.repository

import android.util.Log
import dev.tekofx.bibliotecat.call.LibraryService
import dev.tekofx.bibliotecat.dto.LibraryResponse
import retrofit2.awaitResponse
import java.net.UnknownHostException

class LibraryRepository(
    private val libraryService: LibraryService
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