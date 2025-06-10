package dev.tekofx.bibliotecat.ui.screens.book

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
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import dev.tekofx.bibliotecat.model.search.SearchTypes
import dev.tekofx.bibliotecat.navigation.NavigateDestinations
import dev.tekofx.bibliotecat.ui.IconResource
import dev.tekofx.bibliotecat.ui.components.feedback.Alert
import dev.tekofx.bibliotecat.ui.components.feedback.AlertType
import dev.tekofx.bibliotecat.ui.components.input.SearchBar
import dev.tekofx.bibliotecat.ui.components.input.SelectBottomSheetContent
import dev.tekofx.bibliotecat.ui.components.input.TextIconButton
import dev.tekofx.bibliotecat.ui.viewModels.book.BookViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BookSearchScreen(
    navHostController: NavHostController, bookViewModel: BookViewModel
) {
    // Data
    val results by bookViewModel.results.collectAsState()

    // Input
    val search by bookViewModel.search.collectAsState()
    val catalogs by bookViewModel.catalogs.collectAsState()


    // Loader
    val isLoadingSearch by bookViewModel.isLoadingSearch.collectAsState()
    val canNavigate by bookViewModel.canNavigate.collectAsState()

    // Error
    val errorMessage by bookViewModel.errorMessage.collectAsState()

    // BottomSheets
    val focus = LocalFocusManager.current
    val density = LocalDensity.current
    var showSearchType by remember { mutableStateOf(false) }
    var isScrolling by remember { mutableStateOf(false) }



    LaunchedEffect(canNavigate) {
        if (canNavigate) {
            Log.d("BookSearchScreen", "Found ${results.items.size} elements")
            if (results.items.size == 1) {
                navHostController.navigate("${NavigateDestinations.BOOK_DETAILS_ROUTE}/${results.items[0].id}")
            } else {
                navHostController.navigate(NavigateDestinations.BOOK_RESULTS_ROUTE)
            }
        }
    }

    // Scaffold
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Hidden,
            skipHiddenState = false,
        )
    )

    fun search() {
        focus.clearFocus()
        bookViewModel.search()
    }

    fun closeBottomSheet() {
        scope.launch {
            scaffoldState.bottomSheetState.hide()
            focus.clearFocus()
        }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetSwipeEnabled = !isScrolling,
        sheetPeekHeight = 0.dp,
        sheetContent = {

            if (showSearchType) {
                // Any word, title
                SelectBottomSheetContent(
                    onClose = { closeBottomSheet() },
                    searchArguments = SearchTypes,
                    selectedItem = search.searchType,
                    onItemSelected = {
                        closeBottomSheet()
                        bookViewModel.onSearchTypeChange(it)
                    },
                )
            } else {
                // Where to search
                SelectBottomSheetContent(
                    onClose = { closeBottomSheet() },
                    searchArguments = catalogs,
                    selectedItem = search.catalog,
                    onItemSelected = {
                        closeBottomSheet()
                        bookViewModel.onCatalogChange(it)
                    },
                    showSearchBar = true,
                    maxHeight = 300.dp,
                    onScrolling = { isScrolling = it }
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically)
        ) {
            if (errorMessage.isNotEmpty()) {
                Alert(errorMessage, AlertType.ERROR)
            }
            SearchBar(
                value = search.query,
                onValueChange = bookViewModel::onSearchTextChanged,
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
                    showSearchType = true
                    scope.launch { scaffoldState.bottomSheetState.expand() }
                }
            )

            TextIconButton(
                modifier = Modifier.fillMaxWidth(),
                text = search.catalog.name,
                startIcon = search.catalog.icon,
                onClick = {
                    showSearchType = false
                    scope.launch { scaffoldState.bottomSheetState.expand() }
                }
            )

            TextIconButton(text = "Cerca",
                startIcon = IconResource.fromImageVector(Icons.Outlined.Search),
                enabled = search.query.isNotEmpty() && !isLoadingSearch,
                onClick = {
                    search()
                }
            )

            AnimatedVisibility(visible = isLoadingSearch, enter = slideInVertically {
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

        }
    }

}


