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
import dev.tekofx.biblioteques.model.book.BookCopy
import dev.tekofx.biblioteques.model.book.BookDetails
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


    /**
     * Gets the book from the list of results
     */
    fun getBooksBySearchResult(url: String) {
        Log.d("BookViewModel", "getBooksBySearchResult")
        val response = repository.getHtmlByUrl(url)
        isLoading.postValue(true)
        response.enqueue(object : Callback<BookResponse> {
            override fun onResponse(
                call: Call<BookResponse>, response: Response<BookResponse>
            ) {
                val resultsResponse =
                    response.body()?.results ?: return onFailure(call, Throwable("Not Results"))

                results.postValue(resultsResponse)
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
     * Get the next resultspage. It gets [SearchResults], can be [BookResults] or [SearchResults]
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
     * Gets the [BookCopies][BookCopy] of the full Book copies page
     */
    fun getBookCopies(book: Book) {
        Log.d("BookViewModel", "getBookCopies")

        val permanentUrl = book.bookDetails?.permanentUrl
        if (permanentUrl == null) {
            Log.d("BookViewModel", "getBookCopies permanenturl null")
            return
        }


        val regex = Regex("record=([^~]+)")
        val matchResult = regex.find(permanentUrl)
        val bvalue = matchResult?.groupValues?.get(1)
        val value = bvalue?.replace("b", "")
        val bookCopiesUrl =
            "search~S171*cat?/.b$value/.b$value/1,1,1,B/holdings~$value&FF=&1,0,"

        val response = repository.getHtmlByUrl(bookCopiesUrl)

        isLoading.postValue(true)
        response.enqueue(object : Callback<BookResponse> {
            override fun onResponse(
                call: Call<BookResponse>,
                response: Response<BookResponse>
            ) {


                val responseBody =
                    response.body() ?: return onFailure(
                        call,
                        Throwable("No response ${response.code()}")
                    )

                val bookCopies = responseBody.bookCopies
                Log.d("BookViewModel", bookCopies.size.toString())
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


    /**
     * Gets the [BookDetails] of a book from the url of a book.
     * It also gets the [BookCopies][BookCopy] showed in the page of Book details
     */
    fun getBookDetails(bookId: Int) {
        Log.d("BookViewModel", "getBookDetails")
        isLoading.postValue(true)
        val book = filterBook(bookId) ?: return
        currentBook.postValue(book)
        val currentBookResultUrl = book.temporalUrl
        val response = repository.getBookDetails(currentBookResultUrl)
        response.enqueue(object : Callback<BookResponse> {
            override fun onResponse(
                call: Call<BookResponse>,
                response: Response<BookResponse>
            ) {
                val responseBody =
                    response.body() ?: return onFailure(call, Throwable("Not Response"))

                val responseBook =
                    responseBody.book ?: return onFailure(call, Throwable("Book not found"))

                currentBook.postValue(responseBook)
                isLoading.postValue(false)
                getBookCopies(responseBook)

            }

            override fun onFailure(p0: Call<BookResponse>, t: Throwable) {
                Log.e("BookViewModel", t.message.toString())
                errorMessage.postValue(t.message)
                isLoading.postValue(false)
            }
        })

    }


    fun filterBook(id: Int): Book? {
        if (results.value is BookResults) {
            val bookResults = (results.value as BookResults).items
            val currentBookResult2 =
                bookResults.find { book: BookResult -> book.id == id } ?: return null

            return Book(currentBookResult2)

        }
        return null
    }

    fun onSearchTextChanged(text: String) {
        queryText = text
    }

    fun setOnResultsScreen(value: Boolean) {
        onResultsScreen.postValue(value)
    }


}