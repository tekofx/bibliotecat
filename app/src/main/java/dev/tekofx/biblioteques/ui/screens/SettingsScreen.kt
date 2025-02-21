package dev.tekofx.biblioteques.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.tekofx.biblioteques.R
import dev.tekofx.biblioteques.ui.IconResource
import dev.tekofx.biblioteques.ui.components.Section
import dev.tekofx.biblioteques.ui.components.input.SurfaceSwitch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    Icon(Icons.Outlined.Settings, contentDescription = "")
                }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {


            Text(
                "App Setttings",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            SurfaceSwitch(
                value = false,
                title = "Material Theme",
                onValueChange = {},
                iconResource = IconResource.fromImageVector(Icons.Outlined.Create)
            )
            SurfaceSwitch(
                value = false,
                title = "Loading Screen",
                description = "Show the loading screen at startup",
                onValueChange = {},
                iconResource = IconResource.fromImageVector(Icons.Outlined.Refresh)
            )




        }
    }
}




@Preview
@Composable
fun SettingsScreenPreview() {
    SettingsScreen()
}