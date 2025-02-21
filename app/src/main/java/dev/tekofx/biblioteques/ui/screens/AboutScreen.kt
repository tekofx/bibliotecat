package dev.tekofx.biblioteques.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.tekofx.biblioteques.R
import dev.tekofx.biblioteques.ui.IconResource
import dev.tekofx.biblioteques.ui.components.Section

@Composable
fun AboutScreen() {
    Scaffold { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                "About",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Section(
                title = "Version",
                description = "Version 1.0.0",
                iconResource = IconResource.fromImageVector(Icons.Outlined.Info)
            )

            Section(
                title = "Changelog",
                description = "Check the changes in the app",
                iconResource = IconResource.fromDrawableResource(R.drawable.history)
            )

            Section(
                title = "Source Code",
                description = "Check on Github",
                iconResource = IconResource.fromDrawableResource(R.drawable.data_object)
            )

            Section(
                title = "Licenses",
                description = "Used in the code of this app",
                iconResource = IconResource.fromDrawableResource(R.drawable.licence)
            )
        }
    }
}

@Preview
@Composable
fun AboutScreenPreview() {
    AboutScreen()
}