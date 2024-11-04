package dev.tekofx.biblioteques.ui.viewModels.library

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.tekofx.biblioteques.dto.LibraryResponse
import dev.tekofx.biblioteques.model.library.Library
import dev.tekofx.biblioteques.repository.LibraryRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.LocalTime


class LibraryViewModel(private val repository: LibraryRepository) : ViewModel() {

    val isLoading = MutableLiveData<Boolean>(false)
    val libraries = MutableLiveData<List<Library>>()
    private val _currentLibrary = MutableLiveData<Library?>()
    val currentLibrary: LiveData<Library?> = _currentLibrary
    val errorMessage = MutableLiveData<String>()
    private var _libraries = MutableLiveData<List<Library>>()
    var queryText by mutableStateOf("")
        private set

    init {
        getLibraries()
    }

    fun getLibrary(pointId: String) {
        Log.d(
            "HomeViewModel",
            "getLibrary called with pointId: $pointId"
        )
        _currentLibrary.postValue(_libraries.value?.find { library: Library -> library.id == pointId })
    }

    fun getLibraries() {
        Log.d("HomeViewModel", "getLibraries called")
        isLoading.postValue(true)
        val response = repository.getLibraries()
        response.enqueue(object : Callback<LibraryResponse> {
            override fun onResponse(
                call: Call<LibraryResponse>,
                response: Response<LibraryResponse>
            ) {

                libraries.postValue(response.body()?.elements)
                _libraries.postValue(response.body()?.elements)
                isLoading.postValue(false)
            }

            override fun onFailure(call: Call<LibraryResponse>, t: Throwable) {
                errorMessage.postValue(t.message)
                isLoading.postValue(false)
            }
        })

    }

    fun onSearchTextChanged(text: String) {
        queryText = text
        libraries.postValue(_libraries.value?.filter {
            it.adrecaNom.contains(
                text,
                ignoreCase = true
            ) || it.municipality.contains(text, ignoreCase = true)
        })
    }

    fun filterOpen(open: Boolean) {
        libraries.postValue(_libraries.value?.filter {
            it.isOpen(LocalDate.now(), LocalTime.now()) == open
        })
    }

}