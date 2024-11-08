package dev.tekofx.biblioteques.ui.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.tekofx.biblioteques.dto.BookResponse
import dev.tekofx.biblioteques.model.book.Book
import dev.tekofx.biblioteques.repository.BookRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class BookViewModel(private val repository: BookRepository) : ViewModel() {
    val books = MutableLiveData<List<Book>>()
    val bookStep = 12

    var thereAreMoreBooks = MutableLiveData<Boolean>(false)
    val isLoading = MutableLiveData<Boolean>(false)
    val currentBook = MutableLiveData<Book?>()
    var totalBooks = mutableIntStateOf(0)
    val indexPage = mutableIntStateOf(1)

    var queryText by mutableStateOf("")
        private set
    val errorMessage = MutableLiveData<String>()


    fun getNextResultsPage() {
        Log.d("BookViewModel", "Get results page ${indexPage.intValue}/${totalBooks.intValue}")
        val response = repository.getResultPage(queryText, indexPage.intValue, totalBooks.intValue)
        isLoading.postValue(true)
        response.enqueue(object : Callback<BookResponse> {
            override fun onResponse(
                call: Call<BookResponse>, response: Response<BookResponse>
            ) {

                val nextBooks = response.body()?.books!!

                val bigList: List<Book> = books.value!!.plus(nextBooks)
                updateThereAreMoreBooks()
                books.postValue(bigList)
                isLoading.postValue(false)
                indexPage.intValue += bookStep
            }

            override fun onFailure(p0: Call<BookResponse>, t: Throwable) {
                Log.e("BookViewModel", "Error getting results page: ${t.message.toString()}")
                errorMessage.postValue("Book not found")
                isLoading.postValue(false)
            }
        })
    }

    fun findBooks() {
        val response = repository.findBooks(queryText)
        isLoading.postValue(true)

        response.enqueue(object : Callback<BookResponse> {
            override fun onResponse(
                call: Call<BookResponse>, response: Response<BookResponse>
            ) {
                val booksResponse = response.body() ?: throw Error()


                Log.d("BookViewModel", "findBooks")
                Log.d("BookViewModel", "totalBooks ${response.body()?.totalBooks}")

                totalBooks.intValue = response.body()?.totalBooks ?: 0
                updateThereAreMoreBooks()
                indexPage.intValue += bookStep
                books.postValue(booksResponse.books)
                isLoading.postValue(false)

            }

            override fun onFailure(p0: Call<BookResponse>, t: Throwable) {
                Log.e("BookViewModel", "Error finding books: ${t.message.toString()}")
                errorMessage.postValue("Error getting books")
                isLoading.postValue(false)

            }

        })

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

    fun updateThereAreMoreBooks() {
        if (indexPage.intValue + bookStep >= totalBooks.intValue) {
            Log.d("BookViewModel", "index ${indexPage.intValue}")
            thereAreMoreBooks.value = false

        } else {
            thereAreMoreBooks.value = true
        }
    }

    fun filterBook(id: Int) {
        currentBook.postValue(books.value?.find { book: Book -> book.id == id })
    }

    fun onSearchTextChanged(text: String) {
        queryText = text
    }


}