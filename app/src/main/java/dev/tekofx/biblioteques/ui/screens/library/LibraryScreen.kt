package dev.tekofx.biblioteques.ui.screens.library


import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import dev.tekofx.biblioteques.call.LibraryService
import dev.tekofx.biblioteques.model.library.Library
import dev.tekofx.biblioteques.model.library.LibraryDummy
import dev.tekofx.biblioteques.model.library.TimeTable
import dev.tekofx.biblioteques.model.library.seasonTranslation
import dev.tekofx.biblioteques.repository.LibraryRepository
import dev.tekofx.biblioteques.ui.IconResource
import dev.tekofx.biblioteques.ui.components.ContactType
import dev.tekofx.biblioteques.ui.components.InfoIntentCard
import dev.tekofx.biblioteques.ui.components.SegmentedButtonItem
import dev.tekofx.biblioteques.ui.components.SegmentedButtons
import dev.tekofx.biblioteques.ui.components.StatusBadge
import dev.tekofx.biblioteques.ui.components.TabEntry
import dev.tekofx.biblioteques.ui.components.TabRowComponent
import dev.tekofx.biblioteques.ui.theme.Typography
import dev.tekofx.biblioteques.ui.viewModels.library.LibraryViewModel
import dev.tekofx.biblioteques.ui.viewModels.library.LibraryViewModelFactory
import dev.tekofx.biblioteques.utils.formatDate
import dev.tekofx.biblioteques.utils.formatDayOfWeek
import java.time.LocalDate
import java.time.LocalTime


val tabEntries = listOf(
    TabEntry("Info", IconResource.fromImageVector(Icons.Outlined.Menu)),
    TabEntry("Com Arribar", IconResource.fromImageVector(Icons.Outlined.LocationOn)),
    TabEntry("Contacta", IconResource.fromImageVector(Icons.Outlined.MailOutline)),
)

@Composable
fun LibraryScreen(
    pointID: String, libraryViewModel: LibraryViewModel = viewModel(
        factory = LibraryViewModelFactory(
            LibraryRepository(LibraryService.getInstance())
        )
    )
) {
    Log.d("LibraryScreen", "Navigated to $pointID")
    val currentLibrary by libraryViewModel.currentLibrary.observeAsState(null)
    val isLoading by libraryViewModel.isLoading.observeAsState(false)
    LaunchedEffect(key1 = Unit) {
        libraryViewModel.getLibrary(pointID)
    }

    currentLibrary?.let { library ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
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
                        { LibraryInfo(library) },
                        { LibraryLocation(library) },
                        { LibraryContact(library) },
                    ),
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    } ?: run {
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),

                ) {

                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        } else {

            Text(text = "No library selected")
        }
    }
}

@Composable
fun LibraryInfo(library: Library) {

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val currentSeasonTimetable = remember { library.getCurrentSeasonTimetable(LocalDate.now()) }
    val nextSeasonTimetable = remember { library.getNextSeasonTimetable(LocalDate.now()) }
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        SegmentedButtons {
            SegmentedButtonItem(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                label = { Text(text = "Horari Actual (${seasonTranslation[currentSeasonTimetable.season]})") },
            )
            SegmentedButtonItem(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                label = { Text(text = "Horari ${seasonTranslation[nextSeasonTimetable.season]}") },
            )

        }
        when (selectedTabIndex) {
            0 -> LibraryTimeTable(currentSeasonTimetable)
            1 -> LibraryTimeTable(nextSeasonTimetable)
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
                tonalElevation = if (it.value.open) 100.dp else 5.dp,
                shape = RoundedCornerShape(10.dp),
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = formatDayOfWeek(it.key),
                        style = Typography.bodyLarge
                    )
                    if (it.value.intervals.size > 1) {
                        Column {
                            Text(
                                text = it.value.intervals[0].toString(),
                                style = Typography.bodyMedium
                            )
                            Text(
                                text = it.value.intervals[1].toString(),
                                style = Typography.bodyMedium
                            )
                        }

                    } else {

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
    LibraryInfo(LibraryDummy)
}