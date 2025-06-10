package dev.tekofx.bibliotecat.ui.screens.about

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.tekofx.bibliotecat.utils.IntentType
import dev.tekofx.bibliotecat.utils.openApp


data class License(
    val name: String,
    val url: String
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResourcesScreen() {

    val licenseCat = License(
        "Llicència oberta d’ús d'informació - Catalunya",
        "https://administraciodigital.gencat.cat/ca/dades/dades-obertes/informacio-practica/llicencies/"
    )
    val licenseCC0 = License("CC0 1.0", "https://opendefinition.org/licenses/cc-zero/")
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Recursos",
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
            ResourceCard(
                title = "Calendari de festes locals a Catalunya",
                description = "Generalitat de Catalunya. Departament de Departament d'Empresa i Treball. Portal de la transparència",
                license = licenseCat,
                url = "https://analisi.transparenciacatalunya.cat/Treball/Calendari-de-festes-locals-a-Catalunya/b4eh-r8up/about_data"
            )
            ResourceCard(
                title = "Festius generals de Catalunya",
                description = "Generalitat de Catalunya. Departament de Departament d'Empresa i Treball. Portal de la transparència",
                license = licenseCat,
                url = "https://analisi.transparenciacatalunya.cat/Treball/Festius-generals-de-Catalunya/8qnu-agns/about_data"
            )
            ResourceCard(
                title = "Xarxa de bibliotecat",
                description = "Diputación de Barcelona. Dades obertes",
                license = licenseCC0,
                url = "https://dadesobertes.diba.cat/datasets/bibliotecat-municipals"
            )

            ResourceCard(
                title = "Catàleg Aladí",
                description = "Xarxa de bibliotecat Municipals de la província de Barcelona",
                url = "https://dadesobertes.diba.cat/datasets/bibliotecat-municipals"
            )

            ResourceCard(
                title = "Directori de bibliotecat Municipals",
                description = "Xarxa de bibliotecat Municipals de la província de Barcelona",
                url = "https://bibliotecavirtual.diba.cat/ca/busca-una-biblioteca"
            )


        }
    }
}

@Composable
fun ResourceCard(
    title: String,
    description: String,
    url: String,
    license: License? = null,
) {
    val context = LocalContext.current

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { openApp(context, IntentType.WEB, url) },
        shape = MaterialTheme.shapes.small,
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
            license?.let {
                LicensePill(license)
            }
        }
    }
}

@Composable
fun LicensePill(
    licence: License
) {
    val context = LocalContext.current
    Surface(
        modifier = Modifier.clickable {
            openApp(context, IntentType.WEB, licence.url)
        },
        shape = RoundedCornerShape(50),
        tonalElevation = 40.dp,
        color = MaterialTheme.colorScheme.primary
    ) {

        Text(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            text = licence.name
        )
    }

}


@Preview
@Composable
fun ResourcesScreenPreview() {
    ResourcesScreen()
}