package dev.tekofx.bibliotecat.ui.components.input

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.tekofx.bibliotecat.ui.IconResource

@Composable
fun TextIconButtonOutlined(
    text: String,
    icon: IconResource,
    onClick: () -> Unit,
    enabled: Boolean = true,
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
    ) {
        Icon(
            modifier = Modifier.padding(start = 0.dp),
            painter = icon.asPainterResource(),
            contentDescription = "",
        )
        Text(text = text)
    }
}
