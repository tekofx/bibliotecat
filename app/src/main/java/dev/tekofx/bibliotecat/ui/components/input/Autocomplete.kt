package dev.tekofx.bibliotecat.ui.components.input

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import dev.tekofx.bibliotecat.ui.components.accordion.AccordionArrow
import java.text.Normalizer

@Composable
fun AutoCompleteSelectBar(
    selectedEntry: String,
    onSelectedEntry: (String) -> Unit,
    entries: List<String>,
    onFocusChange: (Boolean) -> Unit
) {
    val heightTextFields by remember { mutableStateOf(55.dp) }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }
    var expanded by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val topCornerRadius by animateDpAsState(if (expanded) 20.dp else 30.dp, label = "")
    val bottomCornerRadius by animateDpAsState(if (expanded) 0.dp else 30.dp, label = "")

    Column(modifier = Modifier.fillMaxWidth()) {
        SearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .height(heightTextFields)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = {
                        expanded = false
                    }
                )
                .onGloballyPositioned { coordinates ->
                    textFieldSize = coordinates.size.toSize()
                },
            value = selectedEntry,
            onValueChange = {
                onSelectedEntry(it)
                expanded = true
                onFocusChange(true)
            },
            interactionSource = remember { MutableInteractionSource() }
                .also { interactionSource ->
                    LaunchedEffect(interactionSource) {
                        interactionSource.interactions.collect { interaction ->
                            if (interaction is PressInteraction.Release) {
                                expanded = !expanded
                                onFocusChange(expanded)
                            }
                        }
                    }
                },
            label = "Municipi",
            trailingIcon = {
                if (selectedEntry.isNotEmpty()) {
                    Icon(
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                onSelectedEntry("")
                                onFocusChange(false)
                            },
                        imageVector = Icons.Rounded.Clear,
                        contentDescription = "clear",
                    )
                } else {
                    AccordionArrow(expanded)
                }
            },
            shape = RoundedCornerShape(
                topStart = topCornerRadius,
                topEnd = topCornerRadius,
                bottomStart = bottomCornerRadius,
                bottomEnd = bottomCornerRadius
            )
        )

        AnimatedVisibility(visible = expanded) {
            Card(
                modifier = Modifier
                    .width(textFieldSize.width.dp),
                shape = RoundedCornerShape(
                    topStart = 0.dp,
                    topEnd = 0.dp,
                    bottomStart = 20.dp,
                    bottomEnd = 20.dp
                )
            ) {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 200.dp),
                ) {
                    val normalizedSelectedEntry = selectedEntry.normalize()
                    val filteredEntries = if (selectedEntry.isNotEmpty()) {
                        entries.filter {
                            it.normalize().contains(normalizedSelectedEntry, ignoreCase = true)
                        }.sorted()
                    } else {
                        entries.sorted()
                    }

                    items(filteredEntries) {
                        ItemElement(title = it) { title ->
                            onSelectedEntry(title)
                            expanded = false
                            onFocusChange(false)
                        }
                    }
                }
            }
        }
    }
}

fun String.normalize(): String {
    return Normalizer.normalize(this, Normalizer.Form.NFD)
        .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
}

@Composable
fun ItemElement(
    title: String,
    onSelect: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onSelect(title)
            }
            .padding(vertical = 12.dp, horizontal = 15.dp)
    ) {
        Text(text = title, fontSize = 16.sp)
    }
}