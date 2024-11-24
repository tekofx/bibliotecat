package dev.tekofx.biblioteques.ui.components.input

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.tekofx.biblioteques.ui.IconResource

@Composable
fun TextIconButton(
    text: String,
    icon: IconResource,
    onClick: () -> Unit,
    enabled: Boolean = true,
) {
    Button(
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
