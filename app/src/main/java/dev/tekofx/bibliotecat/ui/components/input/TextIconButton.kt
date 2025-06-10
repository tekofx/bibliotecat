package dev.tekofx.bibliotecat.ui.components.input

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.tekofx.bibliotecat.ui.IconResource

@Composable
fun TextIconButton(
    modifier: Modifier = Modifier,
    text: String,
    startIcon: IconResource? = null,
    endIcon: IconResource? = null,
    onClick: () -> Unit,
    enabled: Boolean = true,
) {
    val buttonColor by animateColorAsState(
        targetValue = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(
            alpha = 0.0F
        ),
        animationSpec = tween(300, 0, easing = FastOutSlowInEasing),
        label = "buttonColor"
    )

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor,
            disabledContainerColor = buttonColor
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)
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
