package dev.tekofx.biblioteques.ui.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.tekofx.biblioteques.dto.BookResponse
import dev.tekofx.biblioteques.model.BookResult
import dev.tekofx.biblioteques.model.BookResults
import dev.tekofx.biblioteques.model.SearchResult
import dev.tekofx.biblioteques.model.SearchResults
import dev.tekofx.biblioteques.model.book.Book
import dev.tekofx.biblioteques.repository.BookRepository
import dev.tekofx.biblioteques.ui.components.SearchType
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

val searchTypes = listOf(
    SearchType("Qualsevol paraula", "X"),
    SearchType("Títol", "t"),
    SearchType("Autor/Artista", "a"),
    SearchType("Tema", "d"),
    SearchType("ISBN/ISSN", "i"),
    SearchType("Lloc de publicació de revistas", "m"),
    SearchType("Signatura", "c"),
)


class BookViewModel(private val repository: BookRepository) :
    ViewModel() {
    val results = MutableLiveData<SearchResults<out SearchResult>>()

    val isLoading = MutableLiveData<Boolean>(false)
    val onBookScreen = MutableLiveData<Boolean>(false)
    val onResultsScreen = MutableLiveData<Boolean>(false)
    val currentBookResult = MutableLiveData<BookResult?>()
    val currentBook = MutableLiveData<Book?>()
    private val pageIndex = mutableIntStateOf(0)
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

                val responseBody =
                    response.body() ?: return onFailure(call, Throwable("Not results"))

                val bookResults = responseBody.results as BookResults
                val book = responseBody.book

                if (book != null) {
                    currentBookResult.postValue(bookResults.items.first())
                    currentBook.postValue(book)
                }

                results.postValue(bookResults)
                Log.d("BookViewModel", "getBooksBySearchResult")
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
        val resultsValue = results.value ?: throw Error()
        Log.d("BookViewModel", "Get results page ${pageIndex.intValue}/${resultsValue.numItems}")
        val url = resultsValue.getNextPage()
        val response = repository.getHtmlByUrl(url)
        isLoading.postValue(true)
        response.enqueue(object : Callback<BookResponse> {
            override fun onResponse(
                call: Call<BookResponse>, response: Response<BookResponse>
            ) {
                val responseResults =
                    response.body()?.results ?: return onFailure(call, Throwable("Not Results"))
                val currentResults =
                    results.value as SearchResults<SearchResult>? ?: return onFailure(
                        call,
                        Throwable("CurrentResults is null")
                    )

                currentResults.addItems(responseResults.items)
                results.postValue(currentResults)
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
                val responseResults =
                    response.body()?.results ?: return onFailure(call, Throwable("Not Results"))

                results.postValue(responseResults)
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

        val currentBookResultUrl = currentBookResult.value?.url ?: return

        val response = repository.getBookDetails(currentBookResultUrl)
        isLoading.postValue(true)
        response.enqueue(object : Callback<BookResponse> {
            override fun onResponse(
                call: Call<BookResponse>,
                response: Response<BookResponse>
            ) {


                val bookDetails = response.body()?.bookDetails

                val bookCopies =
                    response.body()?.bookCopies ?: return onFailure(
                        call,
                        Throwable("Book copies not found")
                    )
                val book = currentBook.value ?: return
                book.bookDetails = bookDetails
                book.bookCopies = bookCopies
                currentBook.postValue(book)
                isLoading.postValue(false)
            }

            override fun onFailure(p0: Call<BookResponse>, t: Throwable) {
                Log.e("BookViewModel", t.message.toString())
                errorMessage.postValue(t.message)
                isLoading.postValue(false)
            }
        })

    }


    fun filterBook(id: Int) {
        if (results.value is BookResults) {
            val bookResults = (results.value as BookResults).items
            val currentBookResult2 =
                bookResults.find { book: BookResult -> book.id == id } ?: return

            currentBook.postValue(Book(currentBookResult2))
            currentBookResult.postValue(bookResults.find { book: BookResult -> book.id == id })
        }
    }

    fun onSearchTextChanged(text: String) {
        queryText = text
    }

    fun setOnResultsScreen(value: Boolean) {
        onResultsScreen.postValue(value)
    }


}