package dev.tekofx.biblioteques.ui.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.tekofx.biblioteques.dto.LibraryResponse
import dev.tekofx.biblioteques.model.Library
import dev.tekofx.biblioteques.repository.LibraryRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeViewModel(private val repository: LibraryRepository) : ViewModel() {

    val isLoading = MutableLiveData<Boolean>(false)
    val libraries = MutableLiveData<List<Library>>()
    val errorMessage = MutableLiveData<String>()
    private var _libraries = MutableLiveData<List<Library>>()
    var queryText by mutableStateOf("")
        private set


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
            ) || it.municipiNom.contains(text, ignoreCase = true)
        })


    }
}