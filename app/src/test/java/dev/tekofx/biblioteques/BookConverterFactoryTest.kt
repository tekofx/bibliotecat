package dev.tekofx.biblioteques

import dev.tekofx.biblioteques.call.BookService
import dev.tekofx.biblioteques.model.BookResults
import dev.tekofx.biblioteques.model.GeneralResults
import dev.tekofx.biblioteques.model.SearchResults
import dev.tekofx.biblioteques.model.book.BookConverterFactory
import dev.tekofx.biblioteques.repository.BookRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.Retrofit
import java.io.File
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
@ExperimentalCoroutinesApi
@ExperimentalSerializationApi
class BookConverterFactoryTest {
    private val mockWebServer = MockWebServer()

    private val client = OkHttpClient.Builder()
        .connectTimeout(1, TimeUnit.SECONDS)
        .readTimeout(1, TimeUnit.SECONDS)
        .writeTimeout(1, TimeUnit.SECONDS)
        .build()

    private val api = Retrofit.Builder()
        .baseUrl(mockWebServer.url("/"))
        .client(client)
        .addConverterFactory(BookConverterFactory())
        .build()
        .create(BookService::class.java)


    private val realBookService = BookService.getInstance()
    private val realBookRepository = BookRepository(realBookService)


    private val sut = BookRepository(api)


    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test(expected = Error::class)
    fun `no results`() {
        val htmlResponse = readContentFromFilePath("src/test/resources/book/no_results.html")
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(htmlResponse)
        )

        runBlocking {
            sut.getHtmlByUrl("").execute()
        }
    }

    @Test
    fun `search by any word and get multiple books`() {
        val htmlResponse =
            readContentFromFilePath("src/test/resources/book/search_by_any_word_get_multiple_books.html")
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(htmlResponse)
        )

        runBlocking {
            val response = sut.getHtmlByUrl("").execute()
            assert(response.body()?.results is BookResults)
            assert((response.body()!!.results!!.items.size) == 12)
            assert((response.body()!!.results!!.pages.size) == 2)
            assert((response.body()!!.totalBooks) == 28)

        }
    }

    @Test
    fun `search by any word and get one book`() {
        val htmlResponse =
            readContentFromFilePath("src/test/resources/book/search_by_any_word_get_one_book.html")
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(htmlResponse)
        )

        runBlocking {
            val response = sut.getHtmlByUrl("").execute()
            val results = response.body()!!.results!!
            val bookDetails = response.body()!!.bookDetails!!
            assert(results is BookResults)
            assert((results.items.size) == 1)
            assert((results.pages.size) == 0)
            assert((bookDetails.isbn) == "9788419260284")
            assert((bookDetails.topic) == "Novel·les fantàstiques")
            assert((bookDetails.edition) == "Primera edición")
            assert((bookDetails.collection) == "Waw & Wayne ; 1")
            assert((bookDetails.synopsis) == "Han transcurrido trescientos años desde los acontecimientos de la Triología Original Mistborn. Kelsier y Vin han pasado a formar parte de la historia y la mitología, y el mundo de Scadrial se halla a las puertas de la modernidad. Sin embargo, en las tierras fronterizas conocidas como los Áridos, las antiguas magias todavía son una herramienta crucial para quienes defienden el orden y la justicia.")
            assert((bookDetails.description) == "347 pàgines ; 23 cm")
            assert((bookDetails.bookCopiesUrl) == "search~S171*cat?/.b2084010/.b2084010/1,1,1,B/holdings~2084010&FF=&1,0,")
        }
    }

    @Test
    fun `search by any title and get multiple results`() {
        val htmlResponse =
            readContentFromFilePath("src/test/resources/book/search_by_title_get_multiple_results.html")
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(htmlResponse)
        )

        runBlocking {
            val response = sut.getHtmlByUrl("").execute()
            val results = response.body()!!.results!!
            assert(results is GeneralResults)
            assert(results.items.size == 8)
            assert(results.pages.isEmpty())
            assert(results.numItems == 8)
        }
    }

    @Test
    fun `search by titol`() {
        runBlocking {
            val response = realBookRepository.findBooks("mistborn", "t").execute()
            assert(response.body()?.results is SearchResults)
        }
    }

    @Test
    fun `search by author`() {
        runBlocking {
            val response = realBookRepository.findBooks("sanderson", "a").execute()
            assert(response.body()?.results is SearchResults)
        }
    }

    @Test
    fun `search by topic`() {
        runBlocking {
            val response = realBookRepository.findBooks("plantes", "d").execute()
            assert(response.body()?.results is SearchResults)
        }
    }

    @Test
    fun `search by issn and get list of issns`() {
        runBlocking {
            val response = realBookRepository.findBooks("978", "i").execute()
            assert(response.body()?.results is SearchResults)
            assert((response.body()?.results?.items?.size ?: 0) > 0)

        }
    }

    @Test
    fun `search by issn and get a book`() {
        runBlocking {
            val response = realBookRepository.findBooks("9788419260260", "i").execute()
            assert(response.body()?.results is BookResults)

        }
    }

    @Test
    fun `search by signature and get list`() {
        runBlocking {
            val response = realBookRepository.findBooks("N San", "c").execute()
            assert(response.body()?.results is SearchResults)
            assert((response.body()?.results?.items?.size)!! > 0)

        }
    }

    @Test
    fun `search by signature and not found`() {
        runBlocking {
            val response = realBookRepository.findBooks("san", "c").execute()
            assert(response.body()?.results is SearchResults)
            assert((response.body()?.results?.items?.size) == 0)

        }
    }

    // Utility function to read content from a file private
    private fun readContentFromFilePath(filePath: String): String {
        return File(filePath).readText(
            StandardCharsets.UTF_8
        )
    }
}


