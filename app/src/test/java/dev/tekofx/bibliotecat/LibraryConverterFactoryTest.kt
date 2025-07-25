package dev.tekofx.bibliotecat

import dev.tekofx.bibliotecat.call.HolidayService
import dev.tekofx.bibliotecat.call.LibraryService
import dev.tekofx.bibliotecat.model.holiday.HolidayConverterFactory
import dev.tekofx.bibliotecat.model.library.LibraryConverterFactory
import dev.tekofx.bibliotecat.repository.LibraryRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import retrofit2.Retrofit
import java.io.File
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
@ExperimentalCoroutinesApi
@ExperimentalSerializationApi
class LibraryConverterFactoryTest {
    private val mockWebServer = MockWebServer()

    private val client = OkHttpClient.Builder()
        .connectTimeout(1, TimeUnit.SECONDS)
        .readTimeout(1, TimeUnit.SECONDS)
        .writeTimeout(1, TimeUnit.SECONDS)
        .build()

    private val libraryApi = Retrofit.Builder()
        .baseUrl(mockWebServer.url("/"))
        .client(client)
        .addConverterFactory(LibraryConverterFactory())
        .build()
        .create(LibraryService::class.java)

    private val holidayApi = Retrofit.Builder()
        .baseUrl(mockWebServer.url("/"))
        .client(client)
        .addConverterFactory(HolidayConverterFactory())
        .build()
        .create(HolidayService::class.java)

    private val sut = LibraryRepository(libraryApi)

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `should fetch libraries`() {
        val jsonResponse = readContentFromFilePath("src/test/resources/library_response.json")
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        runBlocking {
            val actual = sut.getLibraries()
            assertNotNull(actual)
            val libraries = actual.elements
            assertNotNull(libraries)
            assertEquals(236, libraries.size)
            assertEquals(LibraryAbrera, libraries[0])
        }
    }

    // Utility function to read content from a file private
    private fun readContentFromFilePath(filePath: String): String {
        return File(filePath).readText(
            StandardCharsets.UTF_8
        )
    }
}