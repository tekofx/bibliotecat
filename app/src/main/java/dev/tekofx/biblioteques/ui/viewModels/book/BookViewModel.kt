package dev.tekofx.biblioteques.ui.viewModels.book

import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.tekofx.biblioteques.dto.BookResponse
import dev.tekofx.biblioteques.exceptions.NotFound
import dev.tekofx.biblioteques.model.book.Book
import dev.tekofx.biblioteques.model.book.BookCopy
import dev.tekofx.biblioteques.model.book.BookCopyAvailability
import dev.tekofx.biblioteques.model.book.BookDetails
import dev.tekofx.biblioteques.model.result.BookResult
import dev.tekofx.biblioteques.model.result.BookResults
import dev.tekofx.biblioteques.model.result.EmptyResults
import dev.tekofx.biblioteques.model.result.SearchResult
import dev.tekofx.biblioteques.model.result.SearchResults
import dev.tekofx.biblioteques.model.search.Search
import dev.tekofx.biblioteques.model.search.SearchArgument
import dev.tekofx.biblioteques.repository.BookRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException


class BookViewModel(private val repository: BookRepository) : ViewModel() {
    // Data
    val searchScopes = MutableStateFlow<List<SearchArgument>>(emptyList())
    private val _results = MutableStateFlow<SearchResults<out SearchResult>>(EmptyResults())
    val results = _results.asStateFlow()
    private val _currentBook = MutableStateFlow<Book?>(null)
    val currentBook = _currentBook.asStateFlow()
    private val _bookCopies = MutableStateFlow<List<BookCopy>>(emptyList())
    private val _areThereMoreCopies = MutableStateFlow(false)
    val areThereMoreCopies = _areThereMoreCopies.asStateFlow()


    // Loaders
    val isLoadingSearch = MutableStateFlow(false) // Navigating to BookResultsScreen
    val isLoadingResults = MutableStateFlow(false) // Loading results in BookResultsScreen
    val isLoadingNextPageResults = MutableStateFlow(false) // Loading next page of results
    val isLoadingBookDetails = MutableStateFlow(false)
    val isLoadingBookCopies = MutableStateFlow(false)
    val isLoadingMoreBookCopies = MutableStateFlow(false)

    // Helpers
    val canNavigateToResults = MutableStateFlow(false)
    private val pageIndex = mutableIntStateOf(0)

    // Inputs
    private val _search = MutableStateFlow(Search())
    val search = _search.asStateFlow()
    private val _availableNowChip = MutableStateFlow(false)
    val availableNowChip = _availableNowChip.asStateFlow()
    private val _canReserveChip = MutableStateFlow(false)
    val canReserveChip = _canReserveChip.asStateFlow()
    private val _bookCopiesTextFieldValue = MutableStateFlow("")
    val bookCopiesTextFieldValue = _bookCopiesTextFieldValue.asStateFlow()


    // Errors
    val errorMessage = MutableStateFlow<String>("")

    // Data filtering
    val bookCopies = _bookCopies
        .combine(_bookCopiesTextFieldValue) { bookCopies, query ->
            if (query.isBlank()) {
                bookCopies
            } else {
                bookCopies.filter { it.location.contains(query, ignoreCase = true) }
            }
        }
        .combine(_availableNowChip) { bookCopies, value ->
            if (value) {
                bookCopies.filter { it.availability == BookCopyAvailability.AVAILABLE }
            } else {
                bookCopies
            }
        }
        .combine(_canReserveChip) { bookCopies, value ->
            if (value) {
                bookCopies.filter { it.availability == BookCopyAvailability.CAN_RESERVE }
            } else {
                bookCopies
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _bookCopies.value
        )

    init {
        getSearchScope()
    }

    /**
     * Gets the search scope from the aladi.diba.cat. This allows user to search in different libraries or catalogs
     * in the same search.
     * The search scope is a list of [SearchArgument] that contains the name of the library and the id of the library.
     *
     */
    private fun getSearchScope() {
        errorMessage.value = ""
        val response = repository.getSearchScope()
        response.enqueue(object : Callback<BookResponse> {
            override fun onResponse(
                call: Call<BookResponse>, response: Response<BookResponse>
            ) {
                val searchScopesResponse =
                    response.body()?.searchScopes ?: return onFailure(
                        call,
                        Throwable("Not searchScopes")
                    )

                searchScopes.value = searchScopesResponse
                errorMessage.value = ""
            }

            override fun onFailure(p0: Call<BookResponse>, t: Throwable) {
                when (t) {
                    is UnknownHostException -> {
                        errorMessage.value = ""
                    }

                    else -> {
                        errorMessage.value = ""
                    }
                }
                Log.e("BookViewModel", "Error getting search Scope: ${t}")
            }

        })
    }


    /**
     * Gets [SearchResults] from the page of results
     */
    fun getResults(url: String) {
        Log.d("BookViewModel", "getResults")
        errorMessage.value = ""
        val response = repository.getHtmlByUrl(url)
        isLoadingResults.value = true
        response.enqueue(object : Callback<BookResponse> {
            override fun onResponse(
                call: Call<BookResponse>, response: Response<BookResponse>
            ) {
                val resultsResponse =
                    response.body()?.results ?: return onFailure(call, NotFound())

                _results.value = resultsResponse
                isLoadingResults.value = false
                errorMessage.value = ""

                Log.d("BookViewModel", "Results: ${resultsResponse.items.size}")
            }

            override fun onFailure(p0: Call<BookResponse>, t: Throwable) {
                when (t) {
                    is NotFound -> {
                        errorMessage.value = "No hi ha resultats"
                    }

                    is UnknownHostException -> {
                        errorMessage.value = "Could not connect to the server"
                    }

                    else -> {
                        errorMessage.value = ""
                    }
                }
                Log.e("BookViewModel", "Error getting results page: $t")
                isLoadingResults.value = false
            }

        })
    }


    /**
     * Get the next [SearchResults] page. It gets [SearchResults], can be [BookResults] or [SearchResults]
     */
    fun getNextResultsPage() {
        errorMessage.value = ""
        Log.d("BookViewModel", "Get results page ${pageIndex.intValue}/${results.value.numItems}")
        val url = results.value.getNextPage() ?: return
        val response = repository.getHtmlByUrl(url)
        isLoadingNextPageResults.value = true
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
                _results.value = currentResults
                isLoadingNextPageResults.value = false
            }

            override fun onFailure(p0: Call<BookResponse>, t: Throwable) {
                Log.e("BookViewModel", "Error getting results page: ${t.message.toString()}")
                errorMessage.value = "Book not found"
                isLoadingNextPageResults.value = false
            }
        })
    }

    /**
     * Searchs a term in aladi.diba.cat. It uses a [queryText] and a [searchTypes]
     *
     */
    fun search() {
        errorMessage.value = ""
        Log.d(
            "BookViewModel",
            "search query: ${search.value}"
        )
        val response = repository.search(
            search.value
        )
        isLoadingSearch.value = true

        response.enqueue(object : Callback<BookResponse> {
            override fun onResponse(
                call: Call<BookResponse>, response: Response<BookResponse>
            ) {
                val responseResults =
                    response.body()?.results ?: return onFailure(call, NotFound())
                _results.value = responseResults
                canNavigateToResults.value = true
                isLoadingSearch.value = false
            }

            override fun onFailure(p0: Call<BookResponse>, t: Throwable) {

                when (t) {
                    is NotFound -> {
                        val message = when (search.value.searchType.value) {
                            "X" -> "Llibre no trobat :("
                            else -> "${search.value.searchType.name} no trobat :("
                        }
                        errorMessage.value = message
                    }

                    is UnknownHostException -> {
                        errorMessage.value = "Error: No hi ha connexió a internet"
                    }

                    is TimeoutException, is SocketTimeoutException -> {
                        errorMessage.value = "Error: Temps d'espera superat"
                        Log.d("BookViewModel", "TimeoutException")
                    }


                }

                Log.e("BookViewModel", "Error finding books: $t")
                isLoadingSearch.value = false
            }
        })
    }

    /**
     * Gets the [BookDetails] of a [Book] from the url of a book.
     */
    fun getBookDetails(bookId: Int) {
        errorMessage.value = ""
        Log.d("BookViewModel", "getBookDetails")
        isLoadingBookDetails.value = true
        val book = filterBook(bookId) ?: return
        _currentBook.value = book
        val currentBookResultUrl = book.url

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

                _currentBook.value = responseBook
                _bookCopies.value = responseBook.bookCopies
                isLoadingBookDetails.value = false
                println("value ${responseBody.thereAreMoreCopies}")
                _areThereMoreCopies.value = responseBody.thereAreMoreCopies
                if (responseBody.thereAreMoreCopies) {
                    //getBookCopies(responseBook)
                    println("There are more books")
                }

            }

            override fun onFailure(p0: Call<BookResponse>, t: Throwable) {
                when (t) {

                    is UnknownHostException -> {
                        errorMessage.value = "Error: No hi ha connexió a internet"
                    }

                    is TimeoutException, is SocketTimeoutException -> {
                        errorMessage.value = "Error: Temps d'espera superat"
                        Log.d("BookViewModel", "TimeoutException")
                    }
                }
                Log.e("BookViewModel", t.message.toString())
                errorMessage.value = t.message!!
                isLoadingBookDetails.value = false
            }
        })

    }


    /**
     * Gets the [BookCopies][BookCopy] of the full book copies page
     */
    fun getMoreBookCopies(book: Book) {
        isLoadingMoreBookCopies.value = true
        errorMessage.value = ""
        Log.d("BookViewModel", "getBookCopies")
        val bookCopiesUrl = book.bookDetails?.bookCopiesUrl ?: return
        Log.d("BookViewModel", "getBookCopies Foung BookCopiesUrl")
        val response = repository.getHtmlByUrl(bookCopiesUrl)

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

                _bookCopies.value = responseBody.bookCopies
                book.bookCopies = responseBody.bookCopies
                _currentBook.value = book
                isLoadingMoreBookCopies.value = false
                _areThereMoreCopies.value = false
            }

            override fun onFailure(p0: Call<BookResponse>, t: Throwable) {
                Log.e("BookViewModel", "GetBookCopies Error: $t")
                errorMessage.value = t.message!!
                isLoadingMoreBookCopies.value = false
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


    fun onBookCopiesTextfieldChange(value: String) {
        _bookCopiesTextFieldValue.value = value
    }

    fun onSearchTypeChange(value: SearchArgument) {
        _search.value = _search.value.copy(searchType = value)
    }

    fun onSearchScopeChange(value: SearchArgument) {
        _search.value = _search.value.copy(searchScope = value)
    }

    /**
     * Callback when Book Search Textfield text is changed
     */
    fun onSearchTextChanged(text: String) {
        _search.value = _search.value.copy(query = text)
    }


    fun resetCurrentBook() {
        _currentBook.value = null
        _bookCopies.value = emptyList()
    }

    fun setCanNavigateToResults(value: Boolean) {
        canNavigateToResults.value = value
    }

    /**
     * Callback when Available Now Chip is clicked
     */
    fun onAvailableNowChipClick() {
        _availableNowChip.value = !_availableNowChip.value
        if (_canReserveChip.value) {
            _canReserveChip.value = false
        }
    }

    /**
     * Callback when Can Reseve Chip is clicked
     */
    fun onCanReserveChipClick() {
        _canReserveChip.value = !_canReserveChip.value
        if (_availableNowChip.value) {
            _availableNowChip.value = false
        }
    }


}