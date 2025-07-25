package dev.tekofx.bibliotecat

import dev.tekofx.bibliotecat.call.BookService
import dev.tekofx.bibliotecat.model.book.BookConverterFactory
import dev.tekofx.bibliotecat.model.result.BookResults
import dev.tekofx.bibliotecat.model.result.GeneralResults
import dev.tekofx.bibliotecat.model.result.SearchResults
import dev.tekofx.bibliotecat.model.search.Search
import dev.tekofx.bibliotecat.model.search.SearchArgument
import dev.tekofx.bibliotecat.model.search.SearchTypeAuthor
import dev.tekofx.bibliotecat.model.search.SearchTypeISBN
import dev.tekofx.bibliotecat.model.search.SearchTypeSignature
import dev.tekofx.bibliotecat.model.search.SearchTypeTopic
import dev.tekofx.bibliotecat.repository.BookRepository
import dev.tekofx.bibliotecat.ui.IconResource
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

    private val allCatalog = SearchArgument(
        "Tot el catàleg",
        "171",
        icon = IconResource.fromDrawableResource(R.drawable.library_books)
    )


    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `no results`() {
        val htmlResponse = readContentFromFilePath("src/test/resources/book/no_results.html")
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(htmlResponse)
        )

        runBlocking {
            val response = sut.getHtmlByUrl("").execute()
            assertNotNull(response.body())
            assertEquals(response.body()!!.results, null)
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
            val results = response.body()!!.results!!
            val totalBooks = response.body()!!.totalBooks
            assert(results is BookResults)
            assertEquals(results.items.size, 12)
            assertEquals(results.pages.size, 2)
            assertEquals(totalBooks, 28)

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
            assertEquals(results.items.size, 1)
            assertEquals(results.pages.size, 0)
            assertEquals(bookDetails.isbn, "9788419260284")
            assertEquals(bookDetails.topics, "Novel·les fantàstiques")
            assertEquals(bookDetails.edition, "Primera edición")
            assertEquals(bookDetails.collections, "Waw & Wayne ; 1")
            assertEquals(
                bookDetails.synopsis,
                "Han transcurrido trescientos años desde los acontecimientos de la Triología Original Mistborn. Kelsier y Vin han pasado a formar parte de la historia y la mitología, y el mundo de Scadrial se halla a las puertas de la modernidad. Sin embargo, en las tierras fronterizas conocidas como los Áridos, las antiguas magias todavía son una herramienta crucial para quienes defienden el orden y la justicia."
            )
            assertEquals(bookDetails.description, "347 pàgines ; 23 cm")
            assertEquals(
                bookDetails.bookCopiesUrl,
                "search~S171*cat?/.b2084010/.b2084010/1,1,1,B/holdings~2084010&FF=&1,0,"
            )
        }
    }

    @Test
    fun `search by title and get multiple general results`() {
        val htmlResponse =
            readContentFromFilePath("src/test/resources/book/search_by_title_get_multiple_general_results.html")
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(htmlResponse)
        )

        runBlocking {
            val response = sut.getHtmlByUrl("").execute()
            val results = response.body()!!.results!!
            assert(results is GeneralResults)
            assertEquals(results.items.size, 8)
            assert(results.pages.isEmpty())
            assertEquals(results.numItems, 8)
        }
    }

    @Test
    fun `search by title and get multiple book results`() {
        val htmlResponse =
            readContentFromFilePath("src/test/resources/book/search_by_title_get_multiple_book_results.html")
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(htmlResponse)
        )

        runBlocking {
            val response = sut.getHtmlByUrl("").execute()
            val results = response.body()!!.results!!
            assert(results is BookResults)
            assertEquals(results.items.size, 6)
            assert(results.pages.isEmpty())
            assertEquals(results.numItems, 6)
        }
    }

    @Test
    fun `search by title and get one book result`() {
        val htmlResponse =
            readContentFromFilePath("src/test/resources/book/search_by_title_get_one_book_result.html")
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(htmlResponse)
        )

        runBlocking {
            val response = sut.getHtmlByUrl("").execute()
            val results = response.body()!!.results!!
            val book = response.body()!!.book!!

            assert(results is BookResults)
            assertEquals(results.items.size, 1)
            assert(results.pages.isEmpty())
            assertEquals(results.numItems, 1)
            assertEquals(book.title, "Aleación de ley : una novela de Mistborn")
            assertEquals(book.author, "Sanderson, Brandon")
        }
    }

    // Test real connections
    @Test
    fun `search by author`() {
        runBlocking {
            val response = realBookRepository.search(
                Search(
                    "sanderson",
                    SearchTypeAuthor,
                    allCatalog
                )
            ).execute()
            assert(response.body()?.results is SearchResults)
        }
    }

    @Test
    fun `search by topic`() {
        runBlocking {
            val response =
                realBookRepository.search(Search("plantes", SearchTypeTopic, allCatalog))
                    .execute()

            assert(response.body()?.results is SearchResults)
        }
    }

    @Test
    fun `search by issn and get list of issns`() {
        runBlocking {
            val response =
                realBookRepository.search(Search("978", SearchTypeISBN, allCatalog))
                    .execute()

            val results = response.body()?.results!!
            assert(response.body()?.results is SearchResults)
            assert(results.items.isNotEmpty())

        }
    }

    @Test
    fun `search by issn and get a book`() {
        runBlocking {
            val response = realBookRepository.search(
                Search(
                    "9788419260260",
                    SearchTypeISBN,
                    allCatalog
                )
            ).execute()
            assert(response.body()?.results is BookResults)

        }
    }

    @Test
    fun `search by signature and get list`() {
        runBlocking {
            val response = realBookRepository.search(
                Search(
                    "N San",
                    SearchTypeSignature,
                    allCatalog
                )
            ).execute()

            val results = response.body()?.results!!
            assert(response.body()?.results is SearchResults)
            assert(results.items.isNotEmpty())
        }
    }

    @Test
    fun `search by signature and not found`() {
        runBlocking {
            val response =
                realBookRepository.search(Search("san", SearchTypeSignature, allCatalog))
                    .execute()
            val results = response.body()?.results!!
            assert(response.body()?.results is SearchResults)
            assert(results.items.isNotEmpty())
        }
    }

    // Utility function to read content from a file private
    private fun readContentFromFilePath(filePath: String): String {
        return File(filePath).readText(
            StandardCharsets.UTF_8
        )
    }
}


