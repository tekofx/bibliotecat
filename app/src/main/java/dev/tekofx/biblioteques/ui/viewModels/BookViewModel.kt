package dev.tekofx.biblioteques.ui.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Document
import com.fleeksoft.ksoup.select.Elements
import dev.tekofx.biblioteques.dto.BookResponse
import dev.tekofx.biblioteques.model.StatusColor
import dev.tekofx.biblioteques.model.book.Book
import dev.tekofx.biblioteques.model.book.BookCopy
import dev.tekofx.biblioteques.repository.BookRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BookViewModel(private val repository: BookRepository) : ViewModel() {
    private var _books = MutableLiveData<List<Book>>()
    val books = MutableLiveData<List<Book>>()
    val isLoading = MutableLiveData<Boolean>(false)
    val currentBook = MutableLiveData<Book>()

    var queryText by mutableStateOf("")
        private set
    val errorMessage = MutableLiveData<String>()

    fun getBook(query: String) {
        val response = repository.getBook(query)
        println(response.toString())
        isLoading.postValue(true)

        response.enqueue(object : Callback<BookResponse> {
            override fun onResponse(
                call: Call<BookResponse>, response: Response<BookResponse>
            ) {
                val test = response.body()?.let { constructBooks(it.body) }

                _books.postValue(test!!)
                books.postValue(test!!)
                isLoading.postValue(false)

            }

            override fun onFailure(p0: Call<BookResponse>, t: Throwable) {
                Log.e("BookViewModel", t.message.toString())
                errorMessage.postValue(t.message)
                isLoading.postValue(false)

            }

        })

    }

    fun constructBooks(html: String): List<Book> {
        val bookList = arrayListOf<Book>()
        Log.d("BookConverterFactory", "1")
        val doc: Document = Ksoup.parse(html = html)
        Log.d("BookConverterFactory", "2")

        val bookElements: Elements = doc.select("td.briefCitRow")
        var index = 0
        for (bookElement in bookElements) {
            val descriptionElement = bookElement.selectFirst("div.descript")
            val titleElement = descriptionElement?.selectFirst("span.titular")?.selectFirst("a")
            val imageElement = bookElement.selectFirst("div.brief_portada")?.selectFirst("img")

            val descriptionFields = descriptionElement.toString().split("<br>")

            val url = titleElement?.attr("href")
            val author = descriptionFields[2].trim()
            val publication = descriptionFields[3].split("<!--")[0].trim()

            if (titleElement != null && imageElement != null) {
                bookList.add(
                    Book(
                        id = index,
                        title = titleElement.text(),
                        author = author,
                        image = imageElement.attr("src"),
                        publication = publication,
                        bookCopies = arrayListOf(),
                        temporalUrl = url!!
                    )
                )
                index++
            }
        }
        return bookList
    }

    private fun constructBookCopies(html: String): List<BookCopy> {
        val doc: Document = Ksoup.parse(html = html)
        val trElements = doc.select("tr.bibItemsEntry")
        val bookCopies = arrayListOf<BookCopy>()
        for (x in trElements) {
            println("w")
            val tdElements = x.select("td")
            val location = tdElements[0].text()
            if (location.isNotEmpty()) {
                val signature = tdElements[1].text()
                val status = tdElements[2].text()
                var notes: String? = tdElements[3].text()
                if (notes!!.isEmpty()) {
                    notes = null
                }

                val statusColor: StatusColor = when {
                    status.contains("Disponible", ignoreCase = true) -> StatusColor.GREEN
                    status.contains("Venç", ignoreCase = true) -> StatusColor.YELLOW
                    else -> StatusColor.RED
                }


                bookCopies.add(
                    BookCopy(
                        location = location,
                        signature = signature,
                        status = status,
                        notes = notes,
                        statusColor = statusColor
                    )
                )
            }
        }
        return bookCopies

    }

    fun getBookDetails() {
        currentBook.value?.let { book ->
            val response = repository.getBookDetails(book.temporalUrl)
            isLoading.postValue(true)
            response.enqueue(object : Callback<BookResponse> {
                override fun onResponse(
                    call: Call<BookResponse>,
                    response: Response<BookResponse>
                ) {
                    val bookCopies = response.body()?.let { constructBookCopies(it.body) }

                    val doc: Document = Ksoup.parse(html = response.body()!!.body)

                    val editionElement =
                        doc.select("td.bibInfoLabel").firstOrNull { it.text() == "Edició" }
                            ?.nextElementSibling()

                    val descriptionElement =
                        doc.select("td.bibInfoLabel").firstOrNull { it.text() == "Descripció" }
                            ?.nextElementSibling()
                    val synopsisElement =
                        doc.select("td.bibInfoLabel").firstOrNull { it.text() == "Sinopsi" }
                            ?.nextElementSibling()

                    val isbnElement =
                        doc.select("td.bibInfoLabel").firstOrNull { it.text() == "ISBN" }
                            ?.nextElementSibling()

                    val edition = editionElement?.text()
                    val description = descriptionElement?.text()
                    val synopsis = synopsisElement?.text()
                    val isbn = isbnElement?.text()
                    bookCopies?.let { copies ->
                        book.bookCopies = copies
                        // Create a new book object with updated bookCopies
                        val updatedBook = Book(
                            id = book.id,
                            title = book.title,
                            author = book.author,
                            temporalUrl = book.temporalUrl,
                            bookCopies = bookCopies,
                            image = book.image,
                            publication = book.publication,
                            edition = edition,
                            description = description,
                            synopsis = synopsis,
                            isbn = isbn
                        )
                        currentBook.postValue(updatedBook)
                    }
                    isLoading.postValue(false)
                }

                override fun onFailure(p0: Call<BookResponse>, t: Throwable) {
                    Log.e("BookViewModel", t.message.toString())
                    errorMessage.postValue(t.message)
                    isLoading.postValue(false)
                }
            })
        }
    }

    fun filterBook(id: Int) {
        currentBook.postValue(_books.value?.find { book: Book -> book.id == id })
    }
}