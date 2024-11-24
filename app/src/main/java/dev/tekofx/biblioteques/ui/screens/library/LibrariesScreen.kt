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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import dev.tekofx.biblioteques.R
import dev.tekofx.biblioteques.call.LibraryService
import dev.tekofx.biblioteques.navigation.NavigateDestinations
import dev.tekofx.biblioteques.repository.LibraryRepository
import dev.tekofx.biblioteques.ui.IconResource
import dev.tekofx.biblioteques.ui.components.Loader
import dev.tekofx.biblioteques.ui.components.input.TextIconButton
import dev.tekofx.biblioteques.ui.components.input.TextIconButtonOutlined
import dev.tekofx.biblioteques.ui.components.library.LibraryList
import dev.tekofx.biblioteques.ui.theme.Typography
import dev.tekofx.biblioteques.ui.viewModels.library.LibraryViewModel
import dev.tekofx.biblioteques.ui.viewModels.library.LibraryViewModelFactory
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LibrariesScreen(
    navHostController: NavHostController,
    libraryViewModel: LibraryViewModel = viewModel(
        factory = LibraryViewModelFactory(
            LibraryRepository(LibraryService.getInstance())
        )
    )
) {
    val libraries by libraryViewModel.libraries.observeAsState(emptyList())
    val municipalities by libraryViewModel.municipalities.observeAsState(emptyList())

    val isLoading by libraryViewModel.isLoading.observeAsState(false)
    var showBottomSheet by remember { mutableStateOf(false) }
    val errorMessage by libraryViewModel.errorMessage.observeAsState("")


    Scaffold(
        modifier = Modifier.padding(horizontal = 5.dp),
        floatingActionButton = {
            if (!isLoading) {
                ExtendedFloatingActionButton(
                    text = { Text("Cercar") },
                    icon = { Icon(Icons.Filled.Search, contentDescription = "") },
                    onClick = {
                        showBottomSheet = true
                    }
                )
            }
        }
    ) {

        Loader(
            isLoading, errorMessage
        )

        LibraryList(
            libraries = libraries,
            onLibraryCardClick = {
                navHostController.navigate("${NavigateDestinations.LIBRARY_DETAILS_ROUTE}?pointId=${it}")
            }
        )

        SearchBottomSheet(
            municipalities = municipalities,
            textFieldValue = libraryViewModel.queryText,
            show = showBottomSheet,
            showOnlyOpen = libraryViewModel.showOnlyOpen,
            filtersApplied = libraryViewModel.filtersApplied,
            selectedMunicipality = libraryViewModel.selectedMunicipality,
            onShowOnlyOpen = { libraryViewModel.onShowOnlyOpen(it) },
            onSelectedMunicipality = { libraryViewModel.onMunicipalityChanged(it) },
            onToggleShow = { showBottomSheet = !showBottomSheet },
            onTextFieldChange = { text -> libraryViewModel.onSearchTextChanged(text) },
            onClearFilters = { libraryViewModel.clearFilters() }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBottomSheet(
    municipalities: List<String>,
    textFieldValue: String,
    showOnlyOpen: Boolean,
    selectedMunicipality: String,
    show: Boolean,
    filtersApplied: Boolean,
    onToggleShow: () -> Unit,
    onShowOnlyOpen: (value: Boolean) -> Unit,
    onSelectedMunicipality: (String) -> Unit,
    onTextFieldChange: (text: String) -> Unit,
    onClearFilters: () -> Unit

) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    if (show) {
        ModalBottomSheet(
            onDismissRequest = {
                onToggleShow()
            },
            sheetState = sheetState
        ) {
            // Sheet content
            Column(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                TextField(
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    value = textFieldValue,
                    onValueChange = { newText ->
                        onTextFieldChange(newText)
                    },
                    singleLine = true,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = null
                        )
                    },
                    shape = RoundedCornerShape(50.dp),
                    modifier = Modifier
                        .fillMaxWidth(),
                    label = { Text("Nom Biblioteca") }
                )
                AutoCompleteSelectBar(
                    entries = municipalities,
                    onSelectedEntry = onSelectedMunicipality,
                    selectedEntry = selectedMunicipality,
                    placeholder = "Municipi"
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(text = "Obert ara", style = Typography.bodyLarge)
                    Switch(
                        checked = showOnlyOpen,
                        onCheckedChange = {
                            onShowOnlyOpen(it)
                        }
                    )
                }
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
                        icon = IconResource.fromImageVector(Icons.Outlined.Close),
                        onClick = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    onToggleShow()
                                }
                            }
                        }
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

        }
    }
}

