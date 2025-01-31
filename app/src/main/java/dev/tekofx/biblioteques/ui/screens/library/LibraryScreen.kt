package dev.tekofx.biblioteques.ui.screens.library


import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import dev.tekofx.biblioteques.R
import dev.tekofx.biblioteques.model.library.Library
import dev.tekofx.biblioteques.model.library.LibraryDummy
import dev.tekofx.biblioteques.model.library.Season
import dev.tekofx.biblioteques.model.library.SeasonTimeTable
import dev.tekofx.biblioteques.model.library.seasonTranslation
import dev.tekofx.biblioteques.ui.IconResource
import dev.tekofx.biblioteques.ui.components.Accordion
import dev.tekofx.biblioteques.ui.components.InfoIntentCard
import dev.tekofx.biblioteques.ui.components.Loader
import dev.tekofx.biblioteques.ui.components.SmallMap
import dev.tekofx.biblioteques.ui.components.StatusBadge
import dev.tekofx.biblioteques.ui.components.TabEntry
import dev.tekofx.biblioteques.ui.components.TabRowComponent
import dev.tekofx.biblioteques.ui.components.input.SegmentedButtonItem
import dev.tekofx.biblioteques.ui.components.input.SegmentedButtons
import dev.tekofx.biblioteques.ui.theme.Typography
import dev.tekofx.biblioteques.ui.viewModels.library.LibraryViewModel
import dev.tekofx.biblioteques.utils.IntentType
import dev.tekofx.biblioteques.utils.formatDate
import dev.tekofx.biblioteques.utils.formatDayOfWeek
import dev.tekofx.biblioteques.utils.openApp
import java.time.LocalDate
import java.time.LocalTime


val tabEntries = listOf(
    TabEntry("Horaris", IconResource.fromDrawableResource(R.drawable.schedule)),
    TabEntry("Ubicació", IconResource.fromImageVector(Icons.Outlined.LocationOn)),
    TabEntry("Contacta", IconResource.fromImageVector(Icons.Outlined.MailOutline)),
)

@Composable
fun LibraryScreen(
    pointID: String?,
    libraryUrl: String?,
    libraryViewModel: LibraryViewModel,
    navHostController: NavHostController
) {
    Log.d("LibraryScreen", "Navigated to $pointID")

    val currentContext = LocalContext.current

    // Data
    val currentLibrary by libraryViewModel.currentLibrary.collectAsState()

    // Loader
    val isLoading by libraryViewModel.isLoading.collectAsState()

    // Error
    val errorMessage by libraryViewModel.errorMessage.observeAsState("")

    LaunchedEffect(key1 = Unit) {
        Log.d("LibraryScreen", "pointId $pointID libraryUrl $libraryUrl")
        libraryViewModel.getLibrary(pointID, libraryUrl)
    }

    if (currentLibrary == null || isLoading) {
        Loader(isLoading, "Obtenint Data")

    } else {
        currentLibrary?.let { library ->
            Column(
                modifier = Modifier
                    .padding(bottom = 10.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(library.image)
                        .memoryCachePolicy(CachePolicy.ENABLED)
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(R.drawable.local_library),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(2F)
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop
                )
                Column(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .padding(top = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(text = library.adrecaNom, style = Typography.headlineSmall)
                    Text(text = library.municipality, style = Typography.titleLarge)
                    StatusBadge(
                        library.getStatusColor(),
                        library.generateStateMessage(LocalDate.now(), LocalTime.now()),
                        Typography.titleMedium
                    )

                    TabRowComponent(
                        tabEntries = tabEntries,
                        contentScreens = listOf(
                            { LibraryTimetable(library) },
                            {
                                LibraryLocation(library, onMapClick = {
                                    //navHostController.navigate(NavigateDestinations.MAP_ROUTE + "?pointId=$pointID")
                                    openApp(
                                        currentContext,
                                        IntentType.LOCATION,
                                        library.location.joinToString(",")
                                    )
                                })
                            },
                            { LibraryContact(library) },
                        ),
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }

    }

}

@Composable
fun LibraryTimetable(
    library: Library

) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val scrollState = rememberScrollState()
    val summerIcon=IconResource.fromDrawableResource(R.drawable.sunny).asPainterResource()
    val winterIcon=IconResource.fromDrawableResource(R.drawable.ac_unit).asPainterResource()

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(10.dp),

        ) {

        SegmentedButtons {
            SegmentedButtonItem(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                label = { Text(text = "Horari ${seasonTranslation[library.currentSeasonTimetable.season]}") },
                icon = { if(library.currentSeasonTimetable.season==Season.SUMMER) Icon(painter = summerIcon, contentDescription = "Summer") else Icon(painter = winterIcon, contentDescription = "Winter") }
            )
            SegmentedButtonItem(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                label = { Text(text = "Horari ${seasonTranslation[library.nextSeasonSeasonTimeTables.season]}") },
                icon = { if(library.nextSeasonSeasonTimeTables.season==Season.SUMMER) Icon(painter = summerIcon, contentDescription = "Summer") else Icon(painter = winterIcon, contentDescription = "Winter") }

            )

        }
        when (selectedTabIndex) {
            0 -> LibraryTimeTable(library.currentSeasonTimetable)
            1 -> {
                LibraryTimeTable(library.nextSeasonSeasonTimeTables)
            }
        }
    }
}

@Composable
fun LibraryTimeTable(seasonTimeTable: SeasonTimeTable) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            tonalElevation = 20.dp,
            shape = RoundedCornerShape(50.dp)
        ) {

            Text(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                text = "${formatDate(seasonTimeTable.start)} - ${formatDate(seasonTimeTable.end)}",
                style = Typography.bodyLarge
            )
        }

        seasonTimeTable.observation?.let {
            Accordion(
                title = "Observacions",
                description = seasonTimeTable.observation,
                icon = IconResource.fromImageVector(Icons.Outlined.Info)
            )
        }

        seasonTimeTable.dayTimetables.forEach {
            Surface(
                tonalElevation = if (it.key == LocalDate.now().dayOfWeek) 100.dp else if (it.value.open) 5.dp else 1.dp,
                shape = RoundedCornerShape(10.dp),
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = formatDayOfWeek(it.key),
                        style = Typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,

                        ) {
                        Text(
                            text = it.value.toString(),
                            style = Typography.bodyLarge
                        )
                    }
                }


            }
        }
    }
}


@Composable
fun LibraryContact(library: Library) {

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {


        if (library.emails == null && library.phones == null && library.webUrl == null) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(),
                tonalElevation = 20.dp,
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.width(50.dp).height(50.dp),
                        painter = IconResource.fromImageVector(Icons.Outlined.Clear)
                            .asPainterResource(),
                        contentDescription = "No dades de contacte"
                    )
                    Text(
                        modifier = Modifier
                            .padding(20.dp),
                        text = "No dades de contacte"
                    )
                }
            }
        } else {

            library.emails?.forEach {
                InfoIntentCard(
                    contactType = IntentType.MAIL,
                    text = it
                )
            }

            library.phones?.forEach {
                InfoIntentCard(
                    contactType = IntentType.PHONE,
                    text = it
                )
            }

            library.webUrl?.let {
                InfoIntentCard(
                    IntentType.WEB,
                    library.webUrl
                )
            }
        }
    }

}

@Composable
fun LibraryLocation(library: Library, onMapClick: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        InfoIntentCard(IntentType.LOCATION, library.address)
        SmallMap(library, onMapClick)
    }
}


@Preview
@Composable
fun LibraryLocationPreview() {
    LibraryLocation(LibraryDummy, onMapClick = {})
}


@Preview
@Composable
fun LibraryContactPreview() {
    LibraryContact(LibraryDummy)
}


@Preview
@Composable
fun LibraryInfoPreview() {
    LibraryTimetable(LibraryDummy)
}