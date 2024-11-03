package dev.tekofx.biblioteques.screens.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.tekofx.biblioteques.components.SegmentedButtonItem
import dev.tekofx.biblioteques.components.SegmentedButtons
import dev.tekofx.biblioteques.components.library.LibraryTimeTable
import dev.tekofx.biblioteques.model.Library
import dev.tekofx.biblioteques.model.LibraryDummy
import dev.tekofx.biblioteques.model.seasonTranslation
import java.time.LocalDate

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


@Preview
@Composable
fun LibraryInfoPreview() {
    LibraryInfo(LibraryDummy)
}