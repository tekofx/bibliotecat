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

    // Data
    val libraries = MutableLiveData<List<Library>>()
    private var _libraries = MutableLiveData<List<Library>>()
    val municipalities = MutableLiveData<List<String>>()
    private val _currentLibrary = MutableLiveData<Library?>()
    val currentLibrary: LiveData<Library?> = _currentLibrary

    // Loaders
    val isLoading = MutableLiveData<Boolean>(false)

    // Inputs
    var showOnlyOpen by mutableStateOf(false)
        private set
    var queryText by mutableStateOf("")
        private set
    var filtersApplied by mutableStateOf(false)
        private set
    var selectedMunicipality by mutableStateOf<String>("")
        private set

    // Error
    val errorMessage = MutableLiveData<String>()

    init {
        getLibraries()
    }

    /**
     * Gets a library from [_libraries]. It uses a pointId or a libraryUrl
     * @param pointId
     * @param libraryUrl
     */
    fun getLibrary(pointId: String?, libraryUrl: String?) {
        Log.d(
            "LibraryVewModel",
            "getLibrary called with pointId: $pointId"
        )

        val library: Library? = when {
            pointId != null -> _libraries.value?.find { library: Library -> library.id == pointId }
            libraryUrl != null -> _libraries.value?.find { library: Library ->
                library.bibliotecaVirtualUrl?.contains(
                    libraryUrl
                ) == true
            }

            else -> {
                null
            }
        }

        if (library == null) {
            errorMessage.postValue("Error: No s'ha pogut obtenir la biblioteca")
        } else {
            errorMessage.postValue("")
        }
        _currentLibrary.postValue(library)


    }

    /**
     * Gets libraries from API
     */
    private fun getLibraries() {
        Log.d("LibraryViewModel", "getLibraries")
        isLoading.postValue(true)
        val response = repository.getLibraries()
        response.enqueue(object : Callback<LibraryResponse> {
            override fun onResponse(
                call: Call<LibraryResponse>,
                response: Response<LibraryResponse>
            ) {
                libraries.postValue(response.body()?.elements)
                municipalities.postValue(response.body()?.municipalities)
                _libraries.postValue(response.body()?.elements)
                isLoading.postValue(false)
                errorMessage.postValue("")
            }

            override fun onFailure(call: Call<LibraryResponse>, t: Throwable) {
                Log.d("LibraryViewModel", "getLibraries error ${t.message}")
                errorMessage.postValue("Error: No s'han pogut carregar les biblioteques")
                isLoading.postValue(false)
            }
        })

    }

    /**
     * Callback of TextField
     */
    fun onSearchTextChanged(text: String) {
        queryText = text
        applyFilters()
    }

    /**
     * Callback of Show Open chip
     * @param switchValue Value of the chip
     */
    fun onShowOnlyOpen(switchValue: Boolean) {
        showOnlyOpen = switchValue
        applyFilters()
    }

    /**
     * Callback used when a municipality is selected in Municipaliy Autocomplete
     */
    fun onMunicipalityChanged(municipality: String) {
        selectedMunicipality = municipality
        applyFilters()
    }

    /**
     * Clear all the filters
     */
    fun clearFilters() {
        queryText = ""
        showOnlyOpen = false
        selectedMunicipality = ""
        applyFilters()
    }

    /**
     * Applies all the filters to the list. Sets [libraries] with the filteres libraries
     */
    private fun applyFilters() {
        val filteredLibraries = _libraries.value?.filter { library ->
            val matchesSearchText = library.adrecaNom.contains(
                queryText,
                ignoreCase = true
            )
            val matchesOpenStatus = if (showOnlyOpen) {
                library.isOpen(LocalDate.now(), LocalTime.now())
            } else {
                true
            }
            val matchesMunicipality = if (selectedMunicipality.isNotEmpty()) {
                selectedMunicipality == library.municipality
            } else {
                true
            }

            matchesSearchText && matchesOpenStatus && matchesMunicipality
        } ?: emptyList()
        libraries.postValue(filteredLibraries.sortedBy { it.municipality })
        if (filteredLibraries.isEmpty()) {
            errorMessage.postValue("No s'han trobat biblioteques amb aquests filtres")
        } else {
            errorMessage.postValue("")
        }

        filtersApplied = !(queryText == "" && !showOnlyOpen && selectedMunicipality == "")
    }
}