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
                        id = "1",
                        title = titleElement.text(),
                        author = author,
                        image = imageElement.attr("src"),
                        publication = publication,
                        bookCopies = arrayListOf(),
                        temporalUrl = url!!
                    )
                )
            }
        }
        return bookList
    }

    private fun constructBookCopy(html: String): List<BookCopy> {
        val doc: Document = Ksoup.parse(html = html)
        val trElements = doc.select("tr.bibItemsEntry")
        val bookCopies = arrayListOf<BookCopy>()
        for (x in trElements) {
            val tdElements = x.select("td")
            val location = tdElements[0].text()
            if (location.isNotEmpty()) {
                val signature = tdElements[1].text()
                val status = tdElements[2].text()
                val notes = tdElements[3].text()
                bookCopies.add(
                    BookCopy(
                        location = location,
                        signature = signature,
                        status = status,
                        notes = notes
                    )
                )


            }
        }

        return bookCopies


    }

    fun getBookCopies() {
        currentBook.value?.let { book ->
            val response = repository.getBookCopies(book.temporalUrl)
            isLoading.postValue(true)
            response.enqueue(object : Callback<BookResponse> {
                override fun onResponse(
                    call: Call<BookResponse>,
                    response: Response<BookResponse>
                ) {
                    val bookCopies = response.body()?.let { constructBookCopy(it.body) }
                    println(bookCopies)
                    bookCopies?.let { copies ->
                        book.bookCopies = copies
                        println("a")
                        println("bbok " + book)
                        // Create a new book object with updated bookCopies
                        val updatedBook = Book(
                            id = book.id,
                            title = book.title,
                            author = book.author,
                            temporalUrl = book.temporalUrl,
                            bookCopies = bookCopies,
                            image = book.image,
                            publication = book.publication
                        )
                        currentBook.postValue(updatedBook)
                        println("currentBook " + currentBook.value)
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

    fun filterBook(string: String): Book? {
        println(string)
        println("books_value ${_books.value?.find { book: Book -> book.id == string }}")
        currentBook.postValue(_books.value?.find { book: Book -> book.id == string })
        return _books.value?.find { book: Book -> book.id == string }
    }
}