package dev.tekofx.biblioteques.ui.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
    var totalBooks = mutableIntStateOf(29)
        private set
    val indexPage = mutableIntStateOf(13)

    var queryText by mutableStateOf("")
        private set
    val errorMessage = MutableLiveData<String>()


    fun getResultPage() {
        Log.d("BookViewModel", "index: ${indexPage.value} totalbooks ${totalBooks.intValue}")
        val response = repository.getResultPage(queryText, indexPage.intValue, totalBooks.intValue)
        response.enqueue(object : Callback<BookResponse> {
            override fun onResponse(
                call: Call<BookResponse>, response: Response<BookResponse>
            ) {

                val constructedBooks = response.body()?.let { constructBooks(it.body) }

                _books.postValue(constructedBooks!!)
                books.postValue(constructedBooks!!)
                isLoading.postValue(false)
                indexPage.intValue++
            }

            override fun onFailure(p0: Call<BookResponse>, t: Throwable) {
                Log.e("BookViewModel", t.message.toString())
                errorMessage.postValue(t.message)
                isLoading.postValue(false)
            }
        })
    }

    fun findBooks() {
        val response = repository.findBooks(queryText)
        println(1)
        isLoading.postValue(true)

        response.enqueue(object : Callback<BookResponse> {
            override fun onResponse(
                call: Call<BookResponse>, response: Response<BookResponse>
            ) {
                val booksResponse = response.body()?.books
                Log.d("BookViewModel", "test")

                totalBooks.intValue = response.body()?.totalBooks ?: 0
                _books.postValue(booksResponse!!)
                books.postValue(booksResponse!!)
                isLoading.postValue(false)

            }

            override fun onFailure(p0: Call<BookResponse>, t: Throwable) {
                Log.e("BookViewModel.findBooks", t.message.toString())
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


    fun getBookDetails() {
        currentBook.value?.let { book ->
            val response = repository.getBookDetails(book.temporalUrl)
            isLoading.postValue(true)
            response.enqueue(object : Callback<BookResponse> {
                override fun onResponse(
                    call: Call<BookResponse>,
                    response: Response<BookResponse>
                ) {
                    val bookCopies = response.body()?.bookCopies
                    val bookDetails = response.body()?.bookDetails
                    println(bookDetails)
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
                            bookDetails = bookDetails
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

    fun onSearchTextChanged(text: String) {
        queryText = text
    }
}