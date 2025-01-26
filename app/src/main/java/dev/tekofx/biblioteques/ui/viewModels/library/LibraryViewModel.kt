package dev.tekofx.biblioteques.ui.viewModels.library

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.network.parseGetRequestBlocking
import dev.tekofx.biblioteques.model.library.Library
import dev.tekofx.biblioteques.repository.LibraryRepository
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.UnknownHostException
import java.text.Normalizer
import java.time.LocalDate
import java.time.LocalTime


class LibraryViewModel(private val repository: LibraryRepository) : ViewModel() {

    // Data
    private val _libraries = MutableStateFlow<List<Library>>(emptyList())
    val municipalities = MutableStateFlow<List<String>>(emptyList())
    private val _currentLibrary = MutableStateFlow<Library?>(null)
    val currentLibrary: StateFlow<Library?> = _currentLibrary
    private val _selectedMunicipality = MutableStateFlow("")
    val selectedMunicipality = _selectedMunicipality.asStateFlow()

    // Loaders
    val isLoading = MutableStateFlow<Boolean>(false)


    // Inputs
    private val _queryText = MutableStateFlow("")
    val queryText = _queryText.asStateFlow()
    private val _showOnlyOpen = MutableStateFlow(false)
    val showOnlyOpen = _showOnlyOpen.asStateFlow()
    private val _filtersApplied = MutableStateFlow(false)
    var filtersApplied = _filtersApplied.asStateFlow()

    // Error
    val errorMessage = MutableLiveData<String>()

    // Data filtering
    val libraries = _libraries
        .combine(_queryText) { libraries, query ->
            if (query.isBlank()) {
                libraries
            } else {
                libraries.filter {
                    Normalizer.normalize(it.adrecaNom, Normalizer.Form.NFD)
                        .replace("\\p{M}".toRegex(), "").contains(query, ignoreCase = true)
                }
            }
        }
        .combine(_showOnlyOpen) { libraries, value ->
            libraries.filter {
                if (value) {
                    it.isOpen(LocalDate.now(), LocalTime.now())
                } else {
                    true
                }
            }
        }
        .combine(_selectedMunicipality) { libraries, selectedMunicipality ->
            libraries.filter {
                if (selectedMunicipality.isNotEmpty()) {
                    if (selectedMunicipality == "Barcelona") {
                        it.municipality.contains(selectedMunicipality)
                    } else {
                        it.municipality == selectedMunicipality
                    }
                } else {
                    true
                }
            }
        }
        .combine(_filtersApplied) { libraries, _ ->
            _filtersApplied.value =
                _selectedMunicipality.value.isNotEmpty() || _showOnlyOpen.value || _queryText.value.isNotEmpty()
            libraries
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _libraries.value
        )


    init {
        getLibraries()
    }


    /**
     * Gets a library from [_libraries]. It uses a pointId or a libraryUrl
     * @param pointId
     * @param libraryUrl
     */
    @OptIn(DelicateCoroutinesApi::class)
    fun getLibrary(pointId: String?, libraryUrl: String?) {
        Log.d(
            "LibraryVewModel",
            "getLibrary called with pointId: $pointId"
        )
        isLoading.value = true
        _currentLibrary.value = null

        GlobalScope.launch(Dispatchers.IO) {

            var redirectedUrl = ""

            var library: Library? = when {
                pointId != null -> _libraries.value.find { library: Library -> library.id == pointId }
                libraryUrl != null -> _libraries.value.find { library: Library ->
                    library.bibliotecaVirtualUrl?.contains(
                        libraryUrl
                    ) == true
                }


                else -> {
                    null
                }
            }

            /**
             * Sometimes the URL in a BookCopy is not the same as its corresponding [Library] in [_libraries].
             * But this the URL can redirect to the correct URL. Check if it redirects
             */
            if (library == null && !libraryUrl.isNullOrEmpty()) {
                val doc =
                    Ksoup.parseGetRequestBlocking("https://bibliotecavirtual.diba.cat/$libraryUrl")
                redirectedUrl =
                    doc.location()?.replace(Regex("^https?://bibliotecavirtual\\.diba\\.cat/"), "")
                        ?.removeSuffix("?") ?: ""
            }

            if (redirectedUrl.isNotEmpty() && redirectedUrl != libraryUrl) {
                library =
                    _libraries.value.find { it: Library ->
                        it.bibliotecaVirtualUrl?.contains(
                            redirectedUrl
                        ) == true
                    }
            }

            withContext(Dispatchers.Main) {
                if (library == null) {
                    errorMessage.postValue("Error: No s'ha pogut obtenir la biblioteca")
                } else {
                    errorMessage.postValue("")
                }
                _currentLibrary.value = library
                isLoading.value = false
            }
        }


    }

    /**
     * Gets libraries from API
     */
    fun getLibraries() {
        Log.d("LibraryViewModel", "getLibraries")
        errorMessage.postValue("")   // Clear error message
        isLoading.value = true
        _libraries.value = emptyList()
        viewModelScope.launch {
            try {
                val response = repository.getLibraries()
                _libraries.value = response.elements
                municipalities.value = response.municipalities
                isLoading.value = false
                errorMessage.postValue("")
            } catch (e: UnknownHostException) {
                Log.d("LibraryViewModel", "Error: ${e.message}")
                errorMessage.postValue("Error: No hi ha connexió a internet")
                isLoading.value = false
            } catch (e: Exception) {
                Log.d("LibraryViewModel", "Error: ${e.message}")
                errorMessage.postValue("Error: No s'han pogut obtenir les biblioteques")
                isLoading.value = false
            }
        }
    }


    /**
     * Callback of TextField
     */
    fun onSearchTextChanged(text: String) {
        _queryText.value = text
    }

    /**
     * Callback of Show Open chip
     * @param switchValue Value of the chip
     */
    fun onShowOnlyOpen(switchValue: Boolean) {
        _showOnlyOpen.value = switchValue
    }

    /**
     * Callback used when a municipality is selected in Municipaliy Autocomplete
     */
    fun onMunicipalityChanged(municipality: String) {
        _selectedMunicipality.value = municipality
    }

    /**
     * Clear all the filters
     */
    fun clearFilters() {
        _queryText.value = ""
        _showOnlyOpen.value = false
        _selectedMunicipality.value = ""
        _filtersApplied.value = false
    }
}

