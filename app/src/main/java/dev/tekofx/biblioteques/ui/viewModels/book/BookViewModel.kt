package dev.tekofx.biblioteques.ui.viewModels.book

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.tekofx.biblioteques.R
import dev.tekofx.biblioteques.dto.BookResponse
import dev.tekofx.biblioteques.exceptions.NotFound
import dev.tekofx.biblioteques.model.BookResult
import dev.tekofx.biblioteques.model.BookResults
import dev.tekofx.biblioteques.model.EmptyResults
import dev.tekofx.biblioteques.model.SearchResult
import dev.tekofx.biblioteques.model.SearchResults
import dev.tekofx.biblioteques.model.SelectItem
import dev.tekofx.biblioteques.model.book.Book
import dev.tekofx.biblioteques.model.book.BookCopy
import dev.tekofx.biblioteques.model.book.BookCopyAvailability
import dev.tekofx.biblioteques.model.book.BookDetails
import dev.tekofx.biblioteques.repository.BookRepository
import dev.tekofx.biblioteques.ui.IconResource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.UnknownHostException

val searchTypes = listOf(
    SelectItem("Qualsevol paraula", "X", IconResource.fromDrawableResource(R.drawable.abc)),
    SelectItem("Títol", "t", IconResource.fromDrawableResource(R.drawable.title)),
    SelectItem("Autor/Artista", "a", IconResource.fromImageVector(Icons.Filled.Person)),
    SelectItem("Tema", "d", IconResource.fromDrawableResource(R.drawable.topic)),
    SelectItem("ISBN/ISSN", "i", IconResource.fromDrawableResource(R.drawable.numbers)),
    SelectItem(
        "Lloc de publicació de revistas",
        "m",
        IconResource.fromDrawableResource(R.drawable.location_city)
    ),
    SelectItem("Signatura", "c", IconResource.fromDrawableResource(R.drawable.assignment)),
)

class BookViewModel(private val repository: BookRepository) : ViewModel() {
    // Data
    val searchScopes = MutableStateFlow<List<SelectItem>>(emptyList())
    private val _results = MutableStateFlow<SearchResults<out SearchResult>>(EmptyResults())
    val results = _results.asStateFlow()
    private val _currentBook = MutableStateFlow<Book?>(null)
    val currentBook = _currentBook.asStateFlow()
    private val _bookCopies = MutableStateFlow<List<BookCopy>>(emptyList())


    // Loaders
    val isLoadingSearch = MutableStateFlow(false) // Navigating to BookResultsScreen
    val isLoadingResults = MutableStateFlow(false) // Loading results in BookResultsScreen
    val isLoadingNextPageResults = MutableStateFlow(false) // Loading next page of results
    val isLoadingBookDetails = MutableStateFlow(false)
    val isLoadingBookCopies = MutableStateFlow(false)

    // Helpers
    val canNavigateToResults = MutableStateFlow(false)
    private val pageIndex = mutableIntStateOf(0)

    // Inputs
    private val _queryText = MutableStateFlow("")
    val queryText = _queryText.asStateFlow()
    private val _availableNowChip = MutableStateFlow(false)
    val availableNowChip = _availableNowChip.asStateFlow()
    private val _canReserveChip = MutableStateFlow(false)
    val canReserveChip = _canReserveChip.asStateFlow()
    private val _bookCopiesTextFieldValue = MutableStateFlow("")
    val bookCopiesTextFieldValue = _bookCopiesTextFieldValue.asStateFlow()

    // Any word, title, author...
    val selectedSearchType = MutableStateFlow(searchTypes.first())

    // In all catalog, music, Martorell...
    val selectedSearchScope = MutableStateFlow(
        SelectItem(
            "Tot el catàleg",
            "171",
            icon = IconResource.fromDrawableResource(R.drawable.library_books)
        )
    )

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
        Log.d("BookViewModel", "getBooksBySearchResult")
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
            "search query:$queryText searchType:${selectedSearchType.value.value}"
        )
        val response = repository.findBooks(
            queryText.value,
            selectedSearchType.value.value,
            selectedSearchScope.value.value
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
                        val message = when (selectedSearchType.value.value) {
                            "X" -> "Llibre no trobat :("
                            else -> "${selectedSearchType.value.text} no trobat :("
                        }
                        errorMessage.value = message
                    }

                    is UnknownHostException -> {
                        errorMessage.value = "Error: No hi ha connexió a internet"
                    }
                }

                Log.e("BookViewModel", "Error finding books: ${t.message.toString()}")
                isLoadingSearch.value = false
            }
        })
    }


    /**
     * Gets the [BookCopies][BookCopy] of the full book copies page
     */
    fun getBookCopies(book: Book) {
        errorMessage.value = ""
        Log.d("BookViewModel", "getBookCopies")
        val bookCopiesUrl = book.bookDetails?.bookCopiesUrl ?: return
        Log.d("BookViewModel", "getBookCopies Foung BookCopiesUrl")
        val response = repository.getHtmlByUrl(bookCopiesUrl)

        isLoadingBookCopies.value = true
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
                isLoadingBookCopies.value = false
            }

            override fun onFailure(p0: Call<BookResponse>, t: Throwable) {
                Log.e("BookViewModel", t.message.toString())
                errorMessage.value = t.message!!
                isLoadingBookCopies.value = false
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
                getBookCopies(responseBook)

            }

            override fun onFailure(p0: Call<BookResponse>, t: Throwable) {
                Log.e("BookViewModel", t.message.toString())
                errorMessage.value = t.message!!
                isLoadingBookDetails.value = false
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


    fun onTextFieldValueChange(value: String) {
        _bookCopiesTextFieldValue.value = value
    }

    fun onSearchTypeChange(value: SelectItem) {
        selectedSearchType.value = value
    }

    fun onSearchScopeChange(value: SelectItem) {
        selectedSearchScope.value = value
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

    /**
     * Callback when Book Search Textfield text is changed
     */
    fun onSearchTextChanged(text: String) {
        _queryText.value = text
    }

}