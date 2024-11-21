package dev.tekofx.biblioteques.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import dev.tekofx.biblioteques.R
import dev.tekofx.biblioteques.ui.IconResource


class SearchType(
    val text: String,
    val value: String,
    val icon: IconResource
)

@Composable
fun ButtonSelect(
    options: List<SearchType>,
    selectedOption: SearchType,
    onOptionSelected: (SearchType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        contentAlignment = Alignment.Center
    ) {
        TextIconButton(
            text = "Filtres",
            icon = IconResource.fromDrawableResource(R.drawable.filter_list),
            onClick = { expanded = true }
        )

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    modifier = Modifier.background(if (selectedOption == option) MaterialTheme.colorScheme.inverseOnSurface else Color.Transparent),
                    text = { Text(option.text) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    },
                    trailingIcon = {
                        if (selectedOption == option) {
                            Icon(Icons.Outlined.Check, contentDescription = "")
                        }
                    },
                )
            }
        }
    }
}