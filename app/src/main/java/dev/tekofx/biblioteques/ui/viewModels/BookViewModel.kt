package dev.tekofx.biblioteques.ui.viewModels

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.tekofx.biblioteques.R
import dev.tekofx.biblioteques.dto.BookResponse
import dev.tekofx.biblioteques.model.BookResult
import dev.tekofx.biblioteques.model.BookResults
import dev.tekofx.biblioteques.model.SearchResult
import dev.tekofx.biblioteques.model.SearchResults
import dev.tekofx.biblioteques.model.book.Book
import dev.tekofx.biblioteques.model.book.BookCopy
import dev.tekofx.biblioteques.model.book.BookDetails
import dev.tekofx.biblioteques.repository.BookRepository
import dev.tekofx.biblioteques.ui.IconResource
import dev.tekofx.biblioteques.ui.components.input.SearchType
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

val searchTypes = listOf(
    SearchType("Qualsevol paraula", "X", IconResource.fromImageVector(Icons.Filled.AddCircle)),
    SearchType("Títol", "t", IconResource.fromDrawableResource(R.drawable.title)),
    SearchType("Autor/Artista", "a", IconResource.fromImageVector(Icons.Filled.Person)),
    SearchType("Tema", "d", IconResource.fromDrawableResource(R.drawable.topic)),
    SearchType("ISBN/ISSN", "i", IconResource.fromDrawableResource(R.drawable.numbers)),
    SearchType(
        "Lloc de publicació de revistas",
        "m",
        IconResource.fromDrawableResource(R.drawable.location_city)
    ),
    SearchType("Signatura", "c", IconResource.fromDrawableResource(R.drawable.assignment)),
)

class BookViewModel(private val repository: BookRepository) :
    ViewModel() {
    val results = MutableLiveData<SearchResults<out SearchResult>>()

    val isLoadingResults = MutableLiveData(false)
    val canNavigateToResults = MutableLiveData(false)
    val isLoadingBookDetails = MutableLiveData(false)
    val isLoadingBookCopies = MutableLiveData(false)
    val currentBook = MutableLiveData<Book?>()
    private val pageIndex = mutableIntStateOf(0)
    val selectedSearchType = mutableStateOf(searchTypes.first())

    var queryText by mutableStateOf("")
        private set
    val errorMessage = MutableLiveData<String>()


    /**
     * Gets [SearchResults] from the page of results
     */
    fun getResults(url: String) {
        Log.d("BookViewModel", "getBooksBySearchResult")
        val response = repository.getHtmlByUrl(url)
        isLoadingResults.postValue(true)
        response.enqueue(object : Callback<BookResponse> {
            override fun onResponse(
                call: Call<BookResponse>, response: Response<BookResponse>
            ) {
                val resultsResponse =
                    response.body()?.results ?: return onFailure(call, Throwable("Not Results"))

                results.postValue(resultsResponse)
                isLoadingResults.postValue(false)
                errorMessage.postValue("")
            }

            override fun onFailure(p0: Call<BookResponse>, t: Throwable) {
                Log.e("BookViewModel", "Error getting results page: ${t.message.toString()}")
                errorMessage.postValue("no hi ha resultats")
                isLoadingResults.postValue(false)
            }

        })
    }


    /**
     * Get the next resultspage. It gets [SearchResults], can be [BookResults] or [SearchResults]
     */
    fun getNextResultsPage() {
        val resultsValue = results.value ?: throw Error()
        Log.d("BookViewModel", "Get results page ${pageIndex.intValue}/${resultsValue.numItems}")
        val url = resultsValue.getNextPage() ?: return
        val response = repository.getHtmlByUrl(url)
        isLoadingResults.postValue(true)
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
                isLoadingResults.postValue(false)
            }

            override fun onFailure(p0: Call<BookResponse>, t: Throwable) {
                Log.e("BookViewModel", "Error getting results page: ${t.message.toString()}")
                errorMessage.postValue("Book not found")
                isLoadingResults.postValue(false)
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
        isLoadingResults.postValue(true)

        response.enqueue(object : Callback<BookResponse> {
            override fun onResponse(
                call: Call<BookResponse>, response: Response<BookResponse>
            ) {
                val responseResults =
                    response.body()?.results ?: return onFailure(call, Throwable("Not Results"))

                results.postValue(responseResults)
                canNavigateToResults.postValue(true)
                isLoadingResults.postValue(false)
            }

            override fun onFailure(p0: Call<BookResponse>, t: Throwable) {
                Log.e("BookViewModel", "Error finding books: ${t.message.toString()}")
                errorMessage.postValue("Error getting books")
                isLoadingResults.postValue(false)
            }
        })
    }


    /**
     * Gets the [BookCopies][BookCopy] of the full Book copies page
     */
    fun getBookCopies(book: Book) {
        Log.d("BookViewModel", "getBookCopies")

        val bookCopiesUrl = book.bookDetails?.bookCopiesUrl ?: return
        Log.d("BookViewModel", "getBookCopies Foung BookCopiesUrl")
        val response = repository.getHtmlByUrl(bookCopiesUrl)

        isLoadingBookCopies.postValue(true)
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
                book.bookCopies = bookCopies
                currentBook.postValue(book)
                isLoadingBookCopies.postValue(false)
            }

            override fun onFailure(p0: Call<BookResponse>, t: Throwable) {
                Log.e("BookViewModel", t.message.toString())
                errorMessage.postValue(t.message)
                isLoadingBookCopies.postValue(false)
            }
        })

    }


    /**
     * Gets the [BookDetails] of a book from the url of a book.
     */
    fun getBookDetails(bookId: Int) {
        Log.d("BookViewModel", "getBookDetails")
        isLoadingBookDetails.postValue(true)
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
                isLoadingBookDetails.postValue(false)
                getBookCopies(responseBook)

            }

            override fun onFailure(p0: Call<BookResponse>, t: Throwable) {
                Log.e("BookViewModel", t.message.toString())
                errorMessage.postValue(t.message)
                isLoadingBookDetails.postValue(false)
            }
        })

    }

    /**
     * Gets a [BookResult] from [results] and converts it to [Book]
     * @param id: Id of the book
     * @return [Book] or null if not found
     */
    private fun filterBook(id: Int): Book? {
        if (results.value is BookResults) {
            val bookResults = (results.value as BookResults).items
            val currentBookResult2 =
                bookResults.find { book: BookResult -> book.id == id } ?: return null

            return Book(currentBookResult2)

        }
        return null
    }

    fun removeCurrentBook() {
        currentBook.postValue(null)
    }

    fun setCanNavigateToResults(value: Boolean) {
        canNavigateToResults.postValue(value)
    }

    fun onSearchTextChanged(text: String) {
        queryText = text
    }

}