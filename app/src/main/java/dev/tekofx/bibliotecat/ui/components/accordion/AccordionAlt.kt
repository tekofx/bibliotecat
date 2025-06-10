package dev.tekofx.bibliotecat.ui.components.accordion

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun AccordionAlt(
    header: @Composable () -> Unit, content: @Composable () -> Unit
) {
    var show by remember { mutableStateOf(false) }
    var rotateIcon by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.clickable(onClick = {
            show = !show
            rotateIcon = !rotateIcon
        }, indication = null, // Remove the click animation
            interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }),
        shape = MaterialTheme.shapes.small,
    ) {
        Column {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                header()
                AccordionArrow(rotateIcon)

            }
            if (show) {
                content()
            }
        }
    }
}


@Preview
@Composable
fun AccordionAltPreview() {
    Accordion(header = { Text("Test") }) {
        Text("This is the content inside the accordion.")
    }
}