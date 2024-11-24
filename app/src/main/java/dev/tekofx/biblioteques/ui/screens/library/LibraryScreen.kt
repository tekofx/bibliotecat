package dev.tekofx.biblioteques.ui.screens.library


import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.tekofx.biblioteques.model.library.Library
import dev.tekofx.biblioteques.model.library.LibraryDummy
import dev.tekofx.biblioteques.model.library.TimeTable
import dev.tekofx.biblioteques.model.library.seasonTranslation
import dev.tekofx.biblioteques.ui.IconResource
import dev.tekofx.biblioteques.ui.components.ContactType
import dev.tekofx.biblioteques.ui.components.InfoIntentCard
import dev.tekofx.biblioteques.ui.components.Loader
import dev.tekofx.biblioteques.ui.components.StatusBadge
import dev.tekofx.biblioteques.ui.components.TabEntry
import dev.tekofx.biblioteques.ui.components.TabRowComponent
import dev.tekofx.biblioteques.ui.components.input.SegmentedButtonItem
import dev.tekofx.biblioteques.ui.components.input.SegmentedButtons
import dev.tekofx.biblioteques.ui.theme.Typography
import dev.tekofx.biblioteques.ui.viewModels.library.LibraryViewModel
import dev.tekofx.biblioteques.utils.formatDate
import dev.tekofx.biblioteques.utils.formatDayOfWeek
import java.time.LocalDate
import java.time.LocalTime

val tabEntries = listOf(
    TabEntry("Horaris", IconResource.fromImageVector(Icons.Outlined.Menu)),
    TabEntry("Ubicació", IconResource.fromImageVector(Icons.Outlined.LocationOn)),
    TabEntry("Contacta", IconResource.fromImageVector(Icons.Outlined.MailOutline)),
)

@Composable
fun LibraryScreen(
    pointID: String?,
    libraryUrl: String?,
    libraryViewModel: LibraryViewModel,
) {
    Log.d("LibraryScreen", "Navigated to $pointID")
    val currentLibrary by libraryViewModel.currentLibrary.observeAsState(null)
    val isLoading by libraryViewModel.isLoading.observeAsState(false)
    val errorMessage by libraryViewModel.errorMessage.observeAsState("")

    LaunchedEffect(key1 = Unit) {
        Log.d("LibraryScreen", "pointId $pointID libraryUrl $libraryUrl")
        libraryViewModel.getLibrary(pointID, libraryUrl)
    }

    currentLibrary?.let { library ->
        Column(
            modifier = Modifier
                .padding(bottom = 10.dp)
        ) {

            AsyncImage(
                model = library.image,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2F)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(top = 20.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(text = library.adrecaNom, style = Typography.headlineMedium)
                Text(text = library.municipality, style = Typography.headlineSmall)
                StatusBadge(
                    library.getStatusColor(),
                    library.generateStateMessage(LocalDate.now(), LocalTime.now()),
                    Typography.titleLarge
                )
                TabRowComponent(
                    tabEntries = tabEntries,
                    contentScreens = listOf(
                        { LibraryTimetable(library) },
                        { LibraryLocation(library) },
                        { LibraryContact(library) },
                    ),
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    } ?: run {
        Loader(isLoading, "Obtenint Data", errorMessage)
    }
}

@Composable
fun LibraryTimetable(
    library: Library

) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val scrollState = rememberScrollState()

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
                label = { Text(text = "Horari Actual (${seasonTranslation[library.currentSeasonTimetable.season]})") },
            )
            SegmentedButtonItem(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                label = { Text(text = "Horari ${seasonTranslation[library.nextSeasonTimeTables.season]}") },
            )

        }
        when (selectedTabIndex) {
            0 -> LibraryTimeTable(library.currentSeasonTimetable)
            1 -> {
                LibraryTimeTable(library.nextSeasonTimeTables)
            }
        }
    }
}

@Composable
fun LibraryTimeTable(timeTable: TimeTable) {
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
                text = "${formatDate(timeTable.start)} - ${formatDate(timeTable.end)}",
                style = Typography.bodyLarge
            )
        }

        timeTable.dayTimetables.forEach {
            Surface(
                tonalElevation = if (it.value.open || it.value.observation.isNotEmpty()) 100.dp else 5.dp,
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

        library.emails.forEach {
            InfoIntentCard(
                contactType = ContactType.MAIL,
                text = it
            )
        }

        library.phones.forEach {
            InfoIntentCard(
                contactType = ContactType.PHONE,
                text = it
            )
        }

        if (library.webUrl.isNotEmpty()) {
            InfoIntentCard(
                ContactType.WEB,
                library.webUrl
            )
        }
    }

}

@Composable
fun LibraryLocation(library: Library) {
    InfoIntentCard(ContactType.LOCATION, library.address)
}


@Preview
@Composable
fun LibraryLocationPreview() {
    LibraryLocation(LibraryDummy)
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