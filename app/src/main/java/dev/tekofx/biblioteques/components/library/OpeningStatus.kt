package dev.tekofx.biblioteques.components.library

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.tekofx.biblioteques.R
import dev.tekofx.biblioteques.model.Library
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun OpeningStatus(library: Library) {
    val date = LocalDate.now()
    val time = LocalTime.now()
    var color = R.color.red_closed
    if (library.isOpen(date, time)) {
        color = R.color.green_open
        if (library.isClosingSoon(date, time)) {
            color = R.color.yellow_soon
        }
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 8.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.circle),
            contentDescription = null,
            modifier = Modifier
                .size(20.dp)
                .padding(end = 8.dp),
            colorFilter = ColorFilter.tint(colorResource(id = color))
        )
        Text(text = library.generateStateMessage(date, time), fontSize = 14.sp)
    }
}