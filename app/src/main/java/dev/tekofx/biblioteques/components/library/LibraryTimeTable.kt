package dev.tekofx.biblioteques.components.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.tekofx.biblioteques.model.Library
import dev.tekofx.biblioteques.model.LibraryDummy
import java.time.LocalDate

@Composable
fun LibraryTimeTable(library: Library) {

    val currentTimeTable = library.getCurrentSeasonTimetable(LocalDate.now())

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

        currentTimeTable.dayTimetables.forEach {
            Surface(
                tonalElevation = if (it.value.open) 100.dp else 5.dp,
                shape = RoundedCornerShape(5.dp),
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(it.key.toString())
                    if (it.value.intervals.size > 1) {
                        Column {
                            Text(text = it.value.intervals[0].toString())
                            Text(text = it.value.intervals[1].toString())
                        }

                    } else {

                        Text(text = it.value.toString())
                    }
                }
            }
        }
    }


}

@Preview
@Composable
fun LibraryTimeTablePreview() {
    LibraryTimeTable(LibraryDummy)
}