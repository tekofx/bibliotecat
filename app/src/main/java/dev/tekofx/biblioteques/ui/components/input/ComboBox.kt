package dev.tekofx.biblioteques.ui.components.input

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.tekofx.biblioteques.ui.IconResource
import androidx.compose.material3.DropdownMenuItem as DropdownMenuItem1


class ButtonSelectItem(
    val text: String,
    val value: String,
    val icon: IconResource
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ButtonSelectItem

        if (text != other.text) return false
        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        var result = text.hashCode()
        result = 31 * result + value.hashCode()
        return result
    }

}

@Composable
fun <T> ComboBox(
    buttonText: String,
    buttonIcon: IconResource,
    options: List<T>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    getText: (T) -> String,
    getIcon: (T) -> IconResource?
) {
    var expanded by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        TextIconButton(
            modifier = Modifier.fillMaxWidth(),
            text = buttonText,
            icon = buttonIcon,
            onClick = { expanded = true }
        )

        DropdownMenu(
            modifier = Modifier
                .height(300.dp)
                .fillMaxWidth(),
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem1(
                    modifier = Modifier.background(if (selectedOption == option) MaterialTheme.colorScheme.inverseOnSurface else Color.Transparent),
                    text = { Text(getText(option)) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    },
                    leadingIcon = {

                        getIcon(option)?.let {
                            Icon(
                                painter = it.asPainterResource(),
                                contentDescription = ""
                            )
                        }
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

