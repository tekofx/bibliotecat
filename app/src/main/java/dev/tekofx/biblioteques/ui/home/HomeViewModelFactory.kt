package dev.tekofx.biblioteques.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.tekofx.biblioteques.repository.LibraryRepository

@Suppress("UNCHECKED_CAST")
class HomeViewModelFactory(private val repository: LibraryRepository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            HomeViewModel(this.repository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}