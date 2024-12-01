package dev.tekofx.biblioteques.ui.viewModels.book

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.tekofx.biblioteques.repository.BookRepository
import javax.inject.Singleton

@Singleton
@Suppress("UNCHECKED_CAST")
class BookViewModelFactory(private val repository: BookRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        Log.d("BookViewModelFactory", "create called")
        return if (modelClass.isAssignableFrom(BookViewModel::class.java)) {
            BookViewModel(this.repository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}