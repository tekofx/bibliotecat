package dev.tekofx.biblioteques.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.tekofx.biblioteques.ui.theme.alertError
import dev.tekofx.biblioteques.ui.theme.alertInfo
import dev.tekofx.biblioteques.ui.theme.alertWarning


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
        Card(
            colors = when (alertType) {
                AlertType.INFO -> CardDefaults.cardColors().copy(containerColor = alertInfo)
                AlertType.ERROR -> CardDefaults.cardColors().copy(containerColor = alertError)
                AlertType.WARNING -> CardDefaults.cardColors().copy(containerColor = alertWarning)
            },
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
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