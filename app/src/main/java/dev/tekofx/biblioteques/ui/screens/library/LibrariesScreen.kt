package dev.tekofx.biblioteques.ui.screens.library

import AutoCompleteSelectBar
import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import dev.tekofx.biblioteques.R
import dev.tekofx.biblioteques.navigation.NavigateDestinations
import dev.tekofx.biblioteques.ui.IconResource
import dev.tekofx.biblioteques.ui.components.Alert
import dev.tekofx.biblioteques.ui.components.AlertType
import dev.tekofx.biblioteques.ui.components.Loader
import dev.tekofx.biblioteques.ui.components.input.SearchBar
import dev.tekofx.biblioteques.ui.components.input.SurfaceSwitch
import dev.tekofx.biblioteques.ui.components.input.TextIconButton
import dev.tekofx.biblioteques.ui.components.input.TextIconButtonOutlined
import dev.tekofx.biblioteques.ui.components.library.LibraryList
import dev.tekofx.biblioteques.ui.viewModels.library.LibraryViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LibrariesScreen(
    navHostController: NavHostController,
    libraryViewModel: LibraryViewModel
) {

    // Data
    val municipalities by libraryViewModel.municipalities.collectAsState()
    val libraries by libraryViewModel.libraries.collectAsState()
    val selectedMunicipalityTest by libraryViewModel.selectedMunicipality.collectAsState()

    // Inputs
    val queryText by libraryViewModel.queryText.collectAsState()
    val showOnlyOpenTest by libraryViewModel.showOnlyOpen.collectAsState()
    val filtersApplied by libraryViewModel.filtersApplied.collectAsState()

    var showBottomSheet by remember { mutableStateOf(false) }

    // Loaders
    val isLoading by libraryViewModel.isLoading.collectAsState()

    // Errors
    val errorMessage by libraryViewModel.errorMessage.observeAsState("")
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Hidden,
            skipHiddenState = false
        )
    )

    BottomSheetScaffold(
        modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing),
        scaffoldState = scaffoldState,
        sheetContent = {
            SearchBottomSheet(
                municipalities = municipalities,
                textFieldValue = queryText,
                showOnlyOpen = showOnlyOpenTest,
                filtersApplied = filtersApplied,
                selectedMunicipality = selectedMunicipalityTest,
                onShowOnlyOpen = libraryViewModel::onShowOnlyOpen,
                onSelectedMunicipality = libraryViewModel::onMunicipalityChanged,
                onTextFieldChange = libraryViewModel::onSearchTextChanged,
                onClearFilters = libraryViewModel::clearFilters,
                onClose = { scope.launch { scaffoldState.bottomSheetState.hide() } }
            )
        }
    ) {
        Scaffold(
            modifier = Modifier.padding(horizontal = 5.dp),
            floatingActionButton = {
                LibrariesFAB(
                    show = !isLoading,
                    filtersApplied = filtersApplied,
                    onClick = { scope.launch { scaffoldState.bottomSheetState.expand() } }
                )
            }
        ) {
            PullToRefreshBox(
                isRefreshing = isLoading,
                onRefresh = { libraryViewModel.getLibraries() },
            ) {
                Loader(isLoading, "Obtenint Biblioteques")
                Alert(errorMessage, AlertType.ERROR, floating = true)
                LibraryList(
                    libraries = libraries,
                    filtersApplied = filtersApplied,
                    isLoading = isLoading,
                    onLibraryCardClick = {
                        navHostController.navigate("${NavigateDestinations.LIBRARY_DETAILS_ROUTE}?pointId=${it}")
                    }
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBottomSheet(
    municipalities: List<String>,
    textFieldValue: String,
    showOnlyOpen: Boolean,
    selectedMunicipality: String,
    filtersApplied: Boolean,
    onShowOnlyOpen: (value: Boolean) -> Unit,
    onSelectedMunicipality: (String) -> Unit,
    onTextFieldChange: (text: String) -> Unit,
    onClearFilters: () -> Unit,
    onClose: () -> Unit

) {
    val focusManager = LocalFocusManager.current


    Column(
        modifier = Modifier.padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SearchBar(
            value = textFieldValue,
            onValueChange = onTextFieldChange,
            label = "Nom Biblioteca",
            onDone = {},
        )
        AutoCompleteSelectBar(
            entries = municipalities,
            onSelectedEntry = onSelectedMunicipality,
            selectedEntry = selectedMunicipality,
        )
        SurfaceSwitch(
            value = showOnlyOpen,
            onValueChange = onShowOnlyOpen,
            iconResource = IconResource.fromDrawableResource(R.drawable.schedule),
            title = "Obert ara",
            description = "Mostar només les biblioteques obertes"
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(),
            horizontalArrangement = Arrangement.spacedBy(
                space = 10.dp,
                alignment = Alignment.CenterHorizontally
            )
        ) {

            TextIconButton(
                text = "Tanca",
                startIcon = IconResource.fromImageVector(Icons.Outlined.Close),
                onClick = { onClose() }
            )
            AnimatedVisibility(
                visible = filtersApplied,
                enter = scaleIn() + expandHorizontally(),
                exit = scaleOut() + shrinkHorizontally()
            ) {
                TextIconButtonOutlined(
                    text = "Eliminar filtres",
                    icon = IconResource.fromDrawableResource(R.drawable.filter_list_off),
                    onClick = {
                        focusManager.clearFocus()
                        onClearFilters()
                    }
                )
            }
        }
    }
    Spacer(modifier = Modifier.imePadding())
}


@Composable
fun LibrariesFAB(
    show: Boolean,
    filtersApplied: Boolean,
    onClick: () -> Unit
) {
    if (show) {
        BadgedBox(
            badge = {
                if (filtersApplied) {
                    Badge(
                        modifier = Modifier.size(20.dp),
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                }
            }
        ) {

            ExtendedFloatingActionButton(
                text = { Text("Filtrar") },
                icon = {
                    Icon(
                        IconResource.fromDrawableResource(R.drawable.filter_list)
                            .asPainterResource(),
                        contentDescription = ""
                    )
                },
                onClick = { onClick() }
            )
        }
    }
}
