package dev.tekofx.biblioteques.ui.components.library

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
import androidx.compose.ui.unit.dp
import dev.tekofx.biblioteques.model.library.TimeTable
import dev.tekofx.biblioteques.ui.theme.Typography
import dev.tekofx.biblioteques.utils.formatDate
import dev.tekofx.biblioteques.utils.formatDayOfWeek

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
