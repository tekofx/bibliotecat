package dev.tekofx.biblioteques.ui.components.input

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.tekofx.biblioteques.ui.IconResource

@Composable
fun TextIconButton(
    modifier: Modifier = Modifier,
    text: String,
    startIcon: IconResource? = null,
    endIcon: IconResource? = null,
    onClick: () -> Unit,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (startIcon != null) {
                Icon(
                    modifier = Modifier
                        .padding(start = 0.dp)
                        .height(24.dp),
                    painter = startIcon.asPainterResource(),
                    contentDescription = "",
                )
            }
            Text(text = text)
            if (endIcon != null) {
                Icon(
                    modifier = Modifier
                        .padding(start = 0.dp)
                        .height(24.dp),
                    painter = endIcon.asPainterResource(),
                    contentDescription = "",
                )
            }
        }
    }
}
