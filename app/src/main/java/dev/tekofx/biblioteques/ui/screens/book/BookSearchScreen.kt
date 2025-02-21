package dev.tekofx.biblioteques.ui.screens.book

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import dev.tekofx.biblioteques.model.search.Search
import dev.tekofx.biblioteques.model.search.SearchArgument
import dev.tekofx.biblioteques.model.search.SearchTypes
import dev.tekofx.biblioteques.navigation.NavigateDestinations
import dev.tekofx.biblioteques.ui.IconResource
import dev.tekofx.biblioteques.ui.components.Alert
import dev.tekofx.biblioteques.ui.components.AlertType
import dev.tekofx.biblioteques.ui.components.SelectBottomSheet
import dev.tekofx.biblioteques.ui.components.input.SearchBar
import dev.tekofx.biblioteques.ui.components.input.TextIconButton
import dev.tekofx.biblioteques.ui.viewModels.book.BookViewModel


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BookSearchScreen(
    navHostController: NavHostController, bookViewModel: BookViewModel
) {
    // Data
    val results by bookViewModel.results.collectAsState()

    // Input
    val search by bookViewModel.search.collectAsState()
    val searchScopes by bookViewModel.searchScopes.collectAsState()


    // Loader
    val isLoadingSearch by bookViewModel.isLoadingSearch.collectAsState()
    val canNavigateToResults by bookViewModel.canNavigateToResults.collectAsState()

    // Error
    val errorMessage by bookViewModel.errorMessage.collectAsState()

    LaunchedEffect(canNavigateToResults) {
        if (canNavigateToResults) {
            Log.d("BookSearchScreen", "Found ${results.items.size} elements")
            navHostController.navigate(NavigateDestinations.BOOK_RESULTS_ROUTE)
        }
    }


    Scaffold {
        BookSearch(
            search = search,
            onSearchTextChanged = bookViewModel::onSearchTextChanged,
            onSearchTypeSelected = bookViewModel::onSearchTypeChange,
            onSeachScopeSelected = bookViewModel::onSearchScopeChange,
            errorMessage = errorMessage,
            isLoading = isLoadingSearch,
            onSearch = bookViewModel::search,
            searchScopes = searchScopes,
        )
    }
}

@Composable
fun BookSearch(
    search: Search,
    errorMessage: String?,
    onSearchTextChanged: (String) -> Unit,
    isLoading: Boolean,
    onSearchTypeSelected: (SearchArgument) -> Unit,
    searchScopes: List<SearchArgument>,
    onSeachScopeSelected: (SearchArgument) -> Unit,
    onSearch: () -> Unit
) {
    val focus = LocalFocusManager.current
    val density = LocalDensity.current

    var showSearchTypeBottomSheet by remember { mutableStateOf(false) }
    var showSearchScopeBottomSheet by remember { mutableStateOf(false) }


    fun search() {
        onSearch()
        focus.clearFocus()
    }


    Column(
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically)
    ) {
        if (!errorMessage.isNullOrEmpty()) {
            Alert(errorMessage, AlertType.ERROR)
        }
        SearchBar(
            value = search.query,
            onValueChange = { onSearchTextChanged(it) },
            onDone = { search() },
            trailingIcon = {
                Icon(Icons.Outlined.Search, contentDescription = "")
            },
            label = "Cerca ${search.searchType.name.lowercase()}"
        )

        TextIconButton(
            modifier = Modifier.fillMaxWidth(),
            text = search.searchType.name,
            startIcon = search.searchType.icon,
            onClick = {
                showSearchTypeBottomSheet = !showSearchTypeBottomSheet
            }
        )

        TextIconButton(
            modifier = Modifier.fillMaxWidth(),
            text = search.catalog.name,
            startIcon = search.catalog.icon,
            onClick = {
                showSearchScopeBottomSheet = !showSearchScopeBottomSheet
            }
        )

        TextIconButton(text = "Cerca",
            startIcon = IconResource.fromImageVector(Icons.Outlined.Search),
            enabled = search.query.isNotEmpty() && !isLoading,
            onClick = {
                search()
            }
        )

        AnimatedVisibility(visible = isLoading, enter = slideInVertically {
            // Slide in from 40 dp from the top.
            with(density) { -40.dp.roundToPx() }
        } + expandVertically(
            // Expand from the top.
            expandFrom = Alignment.Top
        ) + fadeIn(
            // Fade in with the initial alpha of 0.3f.
            initialAlpha = 0.3f
        ), exit = slideOutVertically() + shrinkVertically(
            shrinkTowards = Alignment.Bottom
        ) + fadeOut()) {
            CircularProgressIndicator()
        }

        // Any word, title
        SelectBottomSheet(
            show = showSearchTypeBottomSheet,
            onToggleShow = { showSearchTypeBottomSheet = !showSearchTypeBottomSheet },
            searchArguments = SearchTypes,
            selectedItem = search.searchType,
            onItemSelected = onSearchTypeSelected,
        )

        // Where to search
        SelectBottomSheet(
            show = showSearchScopeBottomSheet,
            onToggleShow = { showSearchScopeBottomSheet = !showSearchScopeBottomSheet },
            searchArguments = searchScopes,
            selectedItem = search.catalog,
            onItemSelected = onSeachScopeSelected,
            showSearchBar = true,
            maxHeight = 300.dp
        )


    }
}


