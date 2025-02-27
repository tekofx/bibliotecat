package dev.tekofx.biblioteques.ui.components.input

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.tekofx.biblioteques.model.search.SearchArgument
import dev.tekofx.biblioteques.ui.IconResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectBottomSheetContent(
    onClose: () -> Unit,
    searchArguments: List<SearchArgument>,
    selectedItem: SearchArgument,
    onItemSelected: (SearchArgument) -> Unit,
    showSearchBar: Boolean = false,
    maxHeight: Dp = Dp.Unspecified,
    onScrolling: ((Boolean) -> Unit)? = null
) {
    var textfieldValue by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val currentOnScrolling by rememberUpdatedState(onScrolling)

    val isScrolling by remember {
        derivedStateOf { listState.isScrollInProgress }
    }

    LaunchedEffect(isScrolling) {
        currentOnScrolling?.let { it(isScrolling) }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)

    ) {
        Text(text = "Cerca on ${selectedItem.name}")
        if (showSearchBar) {
            SearchBar(
                value = textfieldValue,
                onValueChange = { textfieldValue = it },
                label = "Filtrar catàlegs",
                trailingIcon = {
                    Icon(Icons.Outlined.Search, contentDescription = "")
                },
            )
        }
        LazyColumn(
            state = listState,
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .heightIn(0.dp, maxHeight),
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
                    }
                )
            }
        }


        TextIconButton(
            text = "Tanca",
            startIcon = IconResource.fromImageVector(Icons.Outlined.Close),
            onClick = { onClose() },
        )
    }
    Spacer(modifier = Modifier.imePadding())
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