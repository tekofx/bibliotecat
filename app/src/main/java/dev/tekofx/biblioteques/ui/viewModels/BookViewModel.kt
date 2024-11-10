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
import dev.tekofx.biblioteques.model.SearchResults
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
    val results = MutableLiveData<SearchResults<out SearchResult>>()

    val isLoading = MutableLiveData<Boolean>(false)
    val currentBook = MutableLiveData<Book?>()
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
                val responseResults =
                    response.body()?.results ?: return onFailure(call, Throwable("Not Results"))

                results.postValue(responseResults)
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
        currentBook.value?.let { book ->
            val response = repository.getBookDetails(book.temporalUrl)
            isLoading.postValue(true)
            response.enqueue(object : Callback<BookResponse> {
                override fun onResponse(
                    call: Call<BookResponse>,
                    response: Response<BookResponse>
                ) {
                    val responseBody = response.body() ?: throw Error()

                    val bookCopies = responseBody.bookCopies
                    val bookDetails = responseBody.bookDetails
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
        //currentBook.postValue(books.value?.find { book: Book -> book.id == id })
    }

    fun onSearchTextChanged(text: String) {
        queryText = text
    }


}