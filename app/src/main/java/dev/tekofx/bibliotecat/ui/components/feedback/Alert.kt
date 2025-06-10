package dev.tekofx.bibliotecat.ui.components.feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.tekofx.bibliotecat.ui.theme.alertError
import dev.tekofx.bibliotecat.ui.theme.alertInfo
import dev.tekofx.bibliotecat.ui.theme.alertWarning


enum class AlertType {
    INFO,
    ERROR,
    WARNING
}

@Composable
fun Alert(
    message: String,
    alertType: AlertType,
    modifier: Modifier = Modifier,
    floating: Boolean = false
) {
    if (message.isEmpty()) return

    Box(
        modifier = if (floating) {
            modifier
                .fillMaxSize()
                .padding(20.dp)
        } else {
            modifier
        },
        contentAlignment = if (floating) Alignment.Center else Alignment.TopStart
    ) {
        Surface(
            modifier = modifier,
            color = when (alertType) {
                AlertType.INFO -> alertInfo
                AlertType.ERROR -> alertError
                AlertType.WARNING -> alertWarning
            },
            shape = MaterialTheme.shapes.medium

        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    when (alertType) {
                        AlertType.INFO -> Icons.Outlined.Info
                        AlertType.ERROR -> Icons.Outlined.Warning
                        AlertType.WARNING -> Icons.Outlined.Warning
                    },
                    contentDescription = "AlertIcon"
                )
                Text(message)
            }
        }
    }
}

@Preview
@Composable
fun AlertPreview() {
    Alert("Message", AlertType.INFO)
}