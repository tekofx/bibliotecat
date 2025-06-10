package dev.tekofx.bibliotecat.ui.viewModels.library

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.tekofx.bibliotecat.repository.LibraryRepository

@Suppress("UNCHECKED_CAST")
class LibraryViewModelFactory(
    private val repository: LibraryRepository,
) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        Log.d("LibraryViewModelFactory", "create called")
        return if (modelClass.isAssignableFrom(LibraryViewModel::class.java)) {
            LibraryViewModel(this.repository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}