package dev.tekofx.bibliotecat.ui.screens.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import dev.tekofx.bibliotecat.R
import dev.tekofx.bibliotecat.navigation.NavigateDestinations
import dev.tekofx.bibliotecat.ui.IconResource
import dev.tekofx.bibliotecat.ui.components.Section
import dev.tekofx.bibliotecat.utils.IntentType
import dev.tekofx.bibliotecat.utils.getAppInfo
import dev.tekofx.bibliotecat.utils.openApp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(navHostController: NavHostController) {

    val context = LocalContext.current
    val appInfo = getAppInfo(context)

    Scaffold { padding ->
        LazyColumn(
            contentPadding = padding,
            modifier = Modifier
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (appInfo != null) {
                item {

                    Image(
                        IconResource.fromDrawableResource(appInfo.icon).asPainterResource(),
                        contentDescription = "Image",
                        modifier = Modifier
                            .size(150.dp),
                        contentScale = ContentScale.Crop
                    )
                }
                item {

                    Text(text = appInfo.appName, style = MaterialTheme.typography.displaySmall)
                }
                item {
                    Section(
                        title = "Version",
                        description = appInfo.versionName,
                        leftIcon = IconResource.fromImageVector(Icons.Outlined.Info)
                    )
                }
            }
            item {
                Text(
                    "App",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            item {
                Section(
                    title = "Registre de canvis",
                    description = "Veure els canvis a l'aplicació",
                    leftIcon = IconResource.fromDrawableResource(R.drawable.history),
                    rightIcon = IconResource.fromDrawableResource(R.drawable.open_in_new),
                    onClick = {
                        openApp(
                            context,
                            IntentType.WEB,
                            context.getString(R.string.changelog_url)
                        )
                    }
                )
            }
            item {
                Section(
                    title = "Codi font",
                    description = "Veure a Github",
                    leftIcon = IconResource.fromDrawableResource(R.drawable.data_object),
                    rightIcon = IconResource.fromDrawableResource(R.drawable.open_in_new),
                    onClick = {
                        openApp(
                            context,
                            IntentType.WEB,
                            context.getString(R.string.source_code_repo)
                        )
                    }
                )
            }
            item {
                Section(
                    title = "Recursos",
                    description = "Utilitzades en el codi d'aquesta aplicació",
                    leftIcon = IconResource.fromDrawableResource(R.drawable.licence),
                    rightIcon = IconResource.fromImageVector(Icons.AutoMirrored.Outlined.ArrowForward),
                    onClick = { navHostController.navigate(NavigateDestinations.RESOURCES_ROUTE) }
                )
            }
            item {
                Text(
                    "Desenvolupador",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            item {
                Section(
                    title = "Donar a Ko-fi",
                    description = "Suporta el desenvolupament de app",
                    leftIcon = IconResource.fromDrawableResource(R.drawable.kofi_symbol),
                    rightIcon = IconResource.fromDrawableResource(R.drawable.open_in_new),
                    tint = Color.Unspecified,
                    onClick = {
                        openApp(
                            context,
                            IntentType.WEB,
                            "https://ko-fi.com/tekofx"
                        )
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun AboutScreenPreview() {
    AboutScreen(rememberNavController())
}