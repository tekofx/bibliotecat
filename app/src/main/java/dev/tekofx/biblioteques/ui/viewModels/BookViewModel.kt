package dev.tekofx.biblioteques.ui.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.tekofx.biblioteques.dto.BookResponse
import dev.tekofx.biblioteques.model.SearchResult
import dev.tekofx.biblioteques.model.book.Book
import dev.tekofx.biblioteques.repository.BookRepository
import dev.tekofx.biblioteques.ui.components.ButtonSelectItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

val searchTypes = listOf(
    ButtonSelectItem("Qualsevol paraula", "X"),
    ButtonSelectItem("Títol", "t"),
    ButtonSelectItem("Autor/Artista", "a"),
    ButtonSelectItem("Tema", "d"),
    ButtonSelectItem("ISBN/ISSN", "i"),
    ButtonSelectItem("Lloc de publicació de revistas", "m"),
    ButtonSelectItem("Signatura", "c"),
)

class BookViewModel(private val repository: BookRepository) : ViewModel() {
    val books = MutableLiveData<List<Book>>()
    val searchResults = MutableLiveData<List<SearchResult>>()
    val pages = mutableStateOf<List<String>>(emptyList())

    val isLoading = MutableLiveData<Boolean>(false)
    val currentBook = MutableLiveData<Book?>()
    var totalBooks = mutableIntStateOf(0)
    val pageIndex = mutableIntStateOf(0)
    val selectedSearchType = mutableStateOf(searchTypes.first())

    var queryText by mutableStateOf("")
        private set
    val errorMessage = MutableLiveData<String>()

    fun getBooksBySearchResult(url: String) {
        Log.d("BookViewModel", "getBooksBySearchResult")
        val response = repository.getHtmlByUrl(url)
        isLoading.postValue(true)
        response.enqueue(object : Callback<BookResponse> {
            override fun onResponse(
                call: Call<BookResponse>, response: Response<BookResponse>
            ) {
                val booksResponse = response.body() ?: throw Error()
                searchResults.postValue(emptyList())
                Log.d("BookViewModel", "getBooksBySearchResult")
                totalBooks.intValue = response.body()?.totalBooks ?: 0
                pages.value = booksResponse.pages
                books.postValue(booksResponse.books)
                isLoading.postValue(false)
            }

            override fun onFailure(p0: Call<BookResponse>, t: Throwable) {
                Log.e("BookViewModel", "Error getting results page: ${t.message.toString()}")
                errorMessage.postValue("Book not found")
                isLoading.postValue(false)
            }

        })
    }


    /**
     * Get the next resultspage
     */
    fun getNextResultsPage() {
        Log.d("BookViewModel", "Get results page ${pageIndex.intValue}/${pages.value.size}")
        println(pages.value.size)
        val url = pages.value[pageIndex.intValue]
        val response = repository.getHtmlByUrl(url)
        isLoading.postValue(true)
        response.enqueue(object : Callback<BookResponse> {
            override fun onResponse(
                call: Call<BookResponse>, response: Response<BookResponse>
            ) {

                val nextBooks = response.body()?.books!!

                val bigList: List<Book> = books.value!!.plus(nextBooks)
                books.postValue(bigList)
                isLoading.postValue(false)
            }

            override fun onFailure(p0: Call<BookResponse>, t: Throwable) {
                Log.e("BookViewModel", "Error getting results page: ${t.message.toString()}")
                errorMessage.postValue("Book not found")
                isLoading.postValue(false)
            }
        })
    }

    /**
     * Searchs a term in aladi. It uses a query and a [searchTypes]
     *
     */
    fun search() {
        Log.d(
            "BookViewModel",
            "search query:$queryText searchType:${selectedSearchType.value.value}"
        )
        val response = repository.findBooks(queryText, selectedSearchType.value.value)
        isLoading.postValue(true)

        response.enqueue(object : Callback<BookResponse> {
            override fun onResponse(
                call: Call<BookResponse>, response: Response<BookResponse>
            ) {
                val booksResponse = response.body() ?: throw Error()

                if (booksResponse.books.isEmpty()) {
                    Log.d("BookViewModel", "search Results ${booksResponse.searchResults}")
                    searchResults.postValue(booksResponse.searchResults)

                } else {
                    Log.d("BookViewModel", "search Books Found")
                    totalBooks.intValue = response.body()?.totalBooks ?: 0
                    pages.value = booksResponse.pages
                    books.postValue(booksResponse.books)
                }
                isLoading.postValue(false)
            }

            override fun onFailure(p0: Call<BookResponse>, t: Throwable) {
                Log.e("BookViewModel", "Error finding books: ${t.message.toString()}")
                errorMessage.postValue("Error getting books")
                isLoading.postValue(false)

            }

        })

    }

    /**
     * Gets the books details of a book from the url of a book
     */
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
        currentBook.postValue(books.value?.find { book: Book -> book.id == id })
    }

    fun onSearchTextChanged(text: String) {
        queryText = text
    }


}