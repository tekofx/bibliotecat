package dev.tekofx.biblioteques.ui.screens.about

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LicensesScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Llicències",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            LicenceCard(
                "Calendari de festes locals a Catalunya",
                "Generalitat de Catalunya. Departament de Departament d'Empresa i Treball. Portal de la transparència",
                "Llicència oberta d’ús d'informació - Catalunya"
            )
            LicenceCard(
                "Festius generals de Catalunya",
                "Generalitat de Catalunya. Departament de Departament d'Empresa i Treball. Portal de la transparència",
                "Llicència oberta d’ús d'informació - Catalunya"
            )
            LicenceCard(
                "Xarxa de Biblioteques",
                "Diputación de Barcelona. Dades obertes",
                "CC0 1.0"
            )

        }
    }
}

@Composable
fun LicenceCard(
    title: String,
    description: String,
    license: String,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        tonalElevation = 10.dp
    ) {

        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(description)
            Surface(
                shape = RoundedCornerShape(50),
                tonalElevation = 40.dp,
                color = MaterialTheme.colorScheme.primary
            ) {

                Text(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    text = license
                )
            }
        }
    }
}


@Preview
@Composable
fun LicensesScreenPreview() {
    LicensesScreen()
}