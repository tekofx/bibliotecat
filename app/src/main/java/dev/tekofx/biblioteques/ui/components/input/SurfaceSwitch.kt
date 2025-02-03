package dev.tekofx.biblioteques.ui.components.input

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.tekofx.biblioteques.ui.IconResource
import dev.tekofx.biblioteques.ui.theme.Typography

@Composable
fun SurfaceSwitch(
    value: Boolean,
    onValueChange: (value: Boolean) -> Unit,
    iconResource: IconResource,
    title: String,
    description: String? = null
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 20.dp,
        shape = MaterialTheme.shapes.extraLarge,
        onClick = {
            onValueChange(!value)
        }
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    iconResource.asPainterResource(),
                    contentDescription = ""
                )
                Column {
                    Text(text = title, style = Typography.bodyLarge)
                    description?.let {
                        Text(text = it, style = Typography.bodySmall)
                    }
                }
            }
            Switch(
                checked = value,
                onCheckedChange = {
                    onValueChange(it)
                }
            )
        }
    }
}