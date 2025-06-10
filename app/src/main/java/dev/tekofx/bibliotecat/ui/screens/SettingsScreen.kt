package dev.tekofx.bibliotecat.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.tekofx.bibliotecat.ui.IconResource
import dev.tekofx.bibliotecat.ui.components.input.SurfaceSwitch
import dev.tekofx.bibliotecat.ui.viewModels.preferences.PreferencesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    preferencesViewModel: PreferencesViewModel
) {
    val dynamicColorEnabled by preferencesViewModel.isDynamicColorEnabled.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets(
                    top = 0.dp,
                    bottom = 0.dp
                ),
                title = {
                    Text(
                        "Configuració", style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                },

                )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                SurfaceSwitch(
                    value = dynamicColorEnabled,
                    title = "Color dinàmic",
                    description = "Utilitzar els colors del fons de pantalla com a colors de l'app",
                    onValueChange = { preferencesViewModel.setDynamicColorEnabled(it) },
                    iconResource = IconResource.fromImageVector(Icons.Outlined.Create)
                )
            }
        }
    }
}


