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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import dev.tekofx.biblioteques.model.EmptyResults
import dev.tekofx.biblioteques.navigation.NavigateDestinations
import dev.tekofx.biblioteques.ui.IconResource
import dev.tekofx.biblioteques.ui.components.input.ButtonSelectItem
import dev.tekofx.biblioteques.ui.components.input.SearchBar
import dev.tekofx.biblioteques.ui.components.input.TextIconButton
import dev.tekofx.biblioteques.ui.viewModels.BookViewModel
import dev.tekofx.biblioteques.ui.viewModels.searchTypes
import kotlinx.coroutines.launch


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BookSearchScreen(
    navHostController: NavHostController, bookViewModel: BookViewModel
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
        BookSearch(errorMessage = errorMessage,
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

    var showSearchTypeButtomSheet by remember { mutableStateOf(false) }
    var showWhereSearchButtomSheet by remember { mutableStateOf(false) }

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

//        ComboBox(buttonText = selectedSearchTpe.text,
//            buttonIcon = selectedSearchTpe.icon,
//            selectedOption = selectedSearchTpe,
//            options = searchTypes,
//            onOptionSelected = {
//                onOptionSelected(it)
//            },
//            getText = { it.text },
//            getIcon = { it.icon }
//        )
        TextIconButton(
            modifier = Modifier.fillMaxWidth(),
            text = selectedSearchTpe.text,
            icon = selectedSearchTpe.icon,
            onClick = {
                showWhereSearchButtomSheet = !showWhereSearchButtomSheet
            }
        )

        TextIconButton(
            modifier = Modifier.fillMaxWidth(),
            text = selectedSearchScope.text,
            icon = selectedSearchScope.icon,
            onClick = {
                showSearchTypeButtomSheet = !showSearchTypeButtomSheet
            }
        )

        TextIconButton(text = "Cerca",
            icon = IconResource.fromImageVector(Icons.Outlined.Search),
            enabled = queryText.isNotEmpty() && !isLoading,
            onClick = {
                search()
            })

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
        SearchTypeBottomSheet(
            show = showSearchTypeButtomSheet,
            onToggleShow = { showSearchTypeButtomSheet = !showSearchTypeButtomSheet },
            searchScopes = searchScopes,
            selectedSearchScope = selectedSearchScope,
            onSeachScopeSelected = onSeachScopeSelected
        )

        WhereSearchBottomSheet(
            show = showWhereSearchButtomSheet,
            onToggleShow = { showWhereSearchButtomSheet = !showWhereSearchButtomSheet },
            searchTypes = searchTypes,
            selectedSearchType = selectedSearchTpe,
            onSearchTypeSelected = onOptionSelected
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTypeBottomSheet(
    show: Boolean,
    onToggleShow: () -> Unit,
    searchScopes: List<ButtonSelectItem>,
    selectedSearchScope: ButtonSelectItem,
    onSeachScopeSelected: (ButtonSelectItem) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    var textfieldValue by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    if (show) {

        ModalBottomSheet(
            onDismissRequest = {
                onToggleShow()
            },
            sheetState = sheetState,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(text = "Cerca on ${selectedSearchScope.text}")


                LazyColumn(
                    modifier = Modifier
                        .height(200.dp)
                        .padding(horizontal = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    items(
                        searchScopes.filter {
                            it.text.lowercase().contains(textfieldValue.lowercase())
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(30.dp)
                                .background(if (selectedSearchScope == it) MaterialTheme.colorScheme.surfaceBright else Color.Transparent)
                                .clickable { onSeachScopeSelected(it) },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = it.text,
                                style = MaterialTheme.typography.titleMedium
                            )

                            if (selectedSearchScope == it) {
                                Icon(
                                    imageVector = Icons.Outlined.Check,
                                    contentDescription = ""
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                    }
                }
                SearchBar(
                    value = textfieldValue,
                    onValueChange = { textfieldValue = it },
                    label = "Filtrar Llocs",
                    trailingIcon = {
                        Icon(Icons.Outlined.Search, contentDescription = "")
                    },
                )
                TextIconButton(
                    text = "Tanca",
                    icon = IconResource.fromImageVector(Icons.Outlined.Close),

                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                onToggleShow()
                            }
                        }
                    }
                )

            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WhereSearchBottomSheet(
    show: Boolean,
    onToggleShow: () -> Unit,
    searchTypes: List<ButtonSelectItem>,
    selectedSearchType: ButtonSelectItem,
    onSearchTypeSelected: (ButtonSelectItem) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val scope = rememberCoroutineScope()
    if (show) {

        ModalBottomSheet(
            onDismissRequest = {
                onToggleShow()
            },
            sheetState = sheetState,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(text = "Cerca on ${selectedSearchType.text}")

                LazyColumn(
                    modifier = Modifier
                        .padding(horizontal = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    items(
                        searchTypes
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(30.dp)
                                .background(if (selectedSearchType == it) MaterialTheme.colorScheme.surfaceBright else Color.Transparent)
                                .clickable { onSearchTypeSelected(it) },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = it.text,
                                style = MaterialTheme.typography.titleMedium
                            )

                            if (selectedSearchType == it) {
                                Icon(
                                    imageVector = Icons.Outlined.Check,
                                    contentDescription = ""
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                    }
                }

                TextIconButton(
                    text = "Tanca",
                    icon = IconResource.fromImageVector(Icons.Outlined.Close),

                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                onToggleShow()
                            }
                        }
                    }
                )

            }
        }
    }
}