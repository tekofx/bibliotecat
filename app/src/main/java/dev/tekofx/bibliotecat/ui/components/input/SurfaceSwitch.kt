package dev.tekofx.bibliotecat.ui.components.input

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.tekofx.bibliotecat.ui.IconResource
import dev.tekofx.bibliotecat.ui.theme.Typography

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
            modifier = Modifier.padding(vertical = 5.dp, horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Icon(
                iconResource.asPainterResource(),
                contentDescription = ""
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(text = title, style = Typography.bodyLarge)
                description?.let {
                    Text(text = it, style = Typography.bodySmall)
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Switch(
                checked = value,
                onCheckedChange = {
                    onValueChange(it)
                }
            )
        }
    }
}

@Preview
@Composable
fun SurfaceSwitchPreview() {
    SurfaceSwitch(
        false,
        title = "Title",
        description = "Description",
        iconResource = IconResource.fromImageVector(Icons.Outlined.PlayArrow),
        onValueChange = {}
    )
}