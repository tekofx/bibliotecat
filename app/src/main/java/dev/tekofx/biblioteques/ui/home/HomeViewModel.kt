package dev.tekofx.biblioteques.ui.home

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.tekofx.biblioteques.dto.LibraryResponse
import dev.tekofx.biblioteques.model.Library
import dev.tekofx.biblioteques.repository.LibraryRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeViewModel(private val repository: LibraryRepository) : ViewModel() {

    val libraries = mutableStateListOf<Library>()
    val errorMessage = MutableLiveData<String>()

    fun getLibraries() {
        val response = repository.getLibraries()

        response.enqueue(object : Callback<LibraryResponse> {
            override fun onResponse(
                call: Call<LibraryResponse>,
                response: Response<LibraryResponse>
            ) {

                libraries.addAll(response.body()?.elements)
            }

            override fun onFailure(call: Call<LibraryResponse>, t: Throwable) {
                errorMessage.postValue(t.message)
            }
        })

    }
}