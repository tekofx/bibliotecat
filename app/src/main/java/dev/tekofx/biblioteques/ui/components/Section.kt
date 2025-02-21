package dev.tekofx.biblioteques.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.tekofx.biblioteques.ui.IconResource

@Composable
fun Section(title: String, description: String, iconResource: IconResource) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 20.dp,
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = iconResource.asPainterResource(),
                contentDescription = ""
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(title, style = MaterialTheme.typography.titleLarge)
                Text(description)
            }
        }
    }
}