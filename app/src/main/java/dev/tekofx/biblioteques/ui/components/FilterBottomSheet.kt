package dev.tekofx.biblioteques.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.tekofx.biblioteques.ui.IconResource
import dev.tekofx.biblioteques.ui.components.input.SearchBar
import dev.tekofx.biblioteques.ui.components.input.SelectItem
import dev.tekofx.biblioteques.ui.components.input.TextIconButton
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    show: Boolean,
    onToggleShow: () -> Unit,
    selectItems: List<SelectItem>,
    selectedItem: SelectItem,
    onItemSelected: (SelectItem) -> Unit,
    showSearchBar: Boolean = false,
    maxHeight: Dp = Dp.Unspecified
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val scope = rememberCoroutineScope()
    var textfieldValue by remember { mutableStateOf("") }
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
                Text(text = "Cerca on ${selectedItem.text}")

                LazyColumn(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .heightIn(max = maxHeight),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    items(
                        selectItems.filter {
                            it.text.lowercase().contains(textfieldValue.lowercase())
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(30.dp)
                                .background(if (selectedItem == it) MaterialTheme.colorScheme.surfaceBright else Color.Transparent)
                                .clickable { onItemSelected(it) },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = it.text,
                                style = MaterialTheme.typography.titleMedium
                            )

                            if (selectedItem == it) {
                                Icon(
                                    imageVector = Icons.Outlined.Check,
                                    contentDescription = ""
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(5.dp))
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