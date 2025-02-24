package dev.tekofx.biblioteques.ui.components.input

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.tekofx.biblioteques.model.search.SearchArgument
import dev.tekofx.biblioteques.ui.IconResource
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectBottomSheet(
    show: Boolean,
    onToggleShow: () -> Unit,
    searchArguments: List<SearchArgument>,
    selectedItem: SearchArgument,
    onItemSelected: (SearchArgument) -> Unit,
    closeOnSelect: Boolean = true,
    showSearchBar: Boolean = false,
    maxHeight: Dp = Dp.Unspecified,
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )


    val scope = rememberCoroutineScope()
    var textfieldValue by remember { mutableStateOf("") }

    fun close() {
        scope.launch { sheetState.hide() }.invokeOnCompletion {
            if (!sheetState.isVisible) {
                onToggleShow()
            }
        }
    }

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
                Text(text = "Cerca on ${selectedItem.name}")

                LazyColumn(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .heightIn(max = maxHeight),
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    items(
                        searchArguments.filter {
                            it.name.lowercase().contains(textfieldValue.lowercase())
                        }
                    ) { item ->
                        Item(
                            item,
                            selectedItem,
                            onItemSelected = {
                                onItemSelected(it)
                                if (closeOnSelect) {
                                    close()
                                }
                            }
                        )
                    }
                }
                if (showSearchBar) {

                    SearchBar(
                        value = textfieldValue,
                        onValueChange = { textfieldValue = it },
                        label = "Filtrar Llocs",
                        trailingIcon = {
                            Icon(Icons.Outlined.Search, contentDescription = "")
                        },
                    )
                }

                TextIconButton(
                    text = "Tanca",
                    startIcon = IconResource.fromImageVector(Icons.Outlined.Close),
                    onClick = { close() },
                )
            }
        }
    }
}

@Composable
fun Item(
    item: SearchArgument,
    selectedItem: SearchArgument,
    onItemSelected: (SearchArgument) -> Unit
) {
    Surface(
        tonalElevation = if (selectedItem == item) 20.dp else 0.dp,
        shape = MaterialTheme.shapes.small,
        onClick = { onItemSelected(item) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {


                Icon(
                    item.icon.asPainterResource(),
                    contentDescription = ""
                )

                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            if (selectedItem == item) {
                Icon(
                    imageVector = Icons.Outlined.Check,
                    contentDescription = ""
                )
            }
        }
    }
}