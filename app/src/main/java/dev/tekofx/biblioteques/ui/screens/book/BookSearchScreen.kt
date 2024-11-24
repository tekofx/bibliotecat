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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import dev.tekofx.biblioteques.model.EmptyResults
import dev.tekofx.biblioteques.navigation.NavigateDestinations
import dev.tekofx.biblioteques.ui.IconResource
import dev.tekofx.biblioteques.ui.components.input.ButtonSelectItem
import dev.tekofx.biblioteques.ui.components.input.ComboBox
import dev.tekofx.biblioteques.ui.components.input.SearchBar
import dev.tekofx.biblioteques.ui.components.input.TextIconButton
import dev.tekofx.biblioteques.ui.viewModels.BookViewModel
import dev.tekofx.biblioteques.ui.viewModels.searchTypes


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BookSearchScreen(
    navHostController: NavHostController,
    bookViewModel: BookViewModel
) {

    val results by bookViewModel.results.observeAsState(
        EmptyResults()
    )
    val searchScopes by bookViewModel.searchScopes.observeAsState(emptyList())

    var selectedSearchScope by bookViewModel.selectedSearchScope
    var selectedSearchTpe by bookViewModel.selectedSearchType

    val isLoadingSearch by bookViewModel.isLoadingSearch.observeAsState(false)
    val search by bookViewModel.canNavigateToResults.observeAsState(false)
    val errorMessage by bookViewModel.errorMessage.observeAsState()

    LaunchedEffect(search) {
        if (search) {
            Log.d("BookSearchScreen", "Found ${results.items.size} elements")
            navHostController.navigate(NavigateDestinations.BOOK_RESULTS_ROUTE)
        }
    }


    Scaffold() {
        BookSearch(
            errorMessage = errorMessage,
            onSearchTextChanged = { bookViewModel.onSearchTextChanged(it) },
            queryText = bookViewModel.queryText,
            isLoading = isLoadingSearch,
            selectedSearchTpe = selectedSearchTpe,
            onSearch = { bookViewModel.search() },
            onOptionSelected = { selectedSearchTpe = it },

            searchScopes = searchScopes,
            selectedSearchScope = selectedSearchScope,
            onSeachScopeSelected = { selectedSearchScope = it }
        )

    }
}

@Composable
fun BookSearch(
    errorMessage: String?,
    searchScopes: List<ButtonSelectItem>,
    selectedSearchScope: ButtonSelectItem,
    onSearchTextChanged: (String) -> Unit,
    queryText: String,
    selectedSearchTpe: ButtonSelectItem,
    isLoading: Boolean,
    onOptionSelected: (ButtonSelectItem) -> Unit,
    onSeachScopeSelected: (ButtonSelectItem) -> Unit,
    onSearch: () -> Unit
) {
    val focus = LocalFocusManager.current
    val density = LocalDensity.current

    var showBottomSheet by remember { mutableStateOf(false) }

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
            Text(text = "Llibre no trobat :(")
        }
        SearchBar(
            value = queryText,
            onValueChange = { onSearchTextChanged(it) },
            onDone = { search() },
            trailingIcon = {
                Icon(Icons.Outlined.Search, contentDescription = "")
            },
            label = "Cerca ${selectedSearchTpe.text.lowercase()}"
        )

        ComboBox(
            buttonText = selectedSearchTpe.text,
            buttonIcon = selectedSearchTpe.icon,
            selectedOption = selectedSearchTpe,
            options = searchTypes,
            onOptionSelected = {
                onOptionSelected(it)
            },
            getText = { it.text },
            getIcon = { it.icon }
        )

        ComboBox(
            buttonText = selectedSearchScope.text,
            buttonIcon = selectedSearchScope.icon,
            selectedOption = selectedSearchScope,
            options = searchScopes,
            onOptionSelected = {
                onSeachScopeSelected(it)
            },
            getText = { it.text },
            getIcon = { null }
        )
        Button(
            onClick = { showBottomSheet = !showBottomSheet }
        ) {
            Text("show")
        }

        TextIconButton(
            text = "Cerca",
            icon = IconResource.fromImageVector(Icons.Outlined.Search),
            enabled = queryText.isNotEmpty() && !isLoading,
            onClick = {
                search()
            }
        )
        AnimatedVisibility(
            visible = isLoading,
            enter = slideInVertically {
                // Slide in from 40 dp from the top.
                with(density) { -40.dp.roundToPx() }
            } + expandVertically(
                // Expand from the top.
                expandFrom = Alignment.Top
            ) + fadeIn(
                // Fade in with the initial alpha of 0.3f.
                initialAlpha = 0.3f
            ),
            exit = slideOutVertically() + shrinkVertically(
                shrinkTowards = Alignment.Bottom
            ) + fadeOut()
        ) {
            CircularProgressIndicator()
        }
        BookSearchBottomsheet(
            show = showBottomSheet,
            onToggleShow = { showBottomSheet = !showBottomSheet },
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookSearchBottomsheet(
    show: Boolean,
    onToggleShow: () -> Unit,

    ) {
    val sheetState = rememberModalBottomSheetState()

    if (show) {
        ModalBottomSheet(
            onDismissRequest = {
                onToggleShow()
            },
            sheetState = sheetState
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = "",
                    onValueChange = {},
                    placeholder = { Text("Cerca") }
                )
                Button(
                    onToggleShow
                ) {
                    Text("Tanca")
                }
            }
        }
    }
}