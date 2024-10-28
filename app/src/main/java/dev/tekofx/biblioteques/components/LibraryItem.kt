package dev.tekofx.biblioteques.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import dev.tekofx.biblioteques.R
import dev.tekofx.biblioteques.model.Library
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun LibraryItem(library: Library) {
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberImagePainter(library.imatge), // Ajusta con tu imagen
            contentDescription = null,
            modifier = Modifier
                .size(100.dp)
                .padding(8.dp),
            contentScale = ContentScale.Crop
        )
        Column(modifier = Modifier.padding(start = 8.dp)) {
            Text(
                text = library.adrecaNom,
                fontSize = 20.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(text = library.municipiNom, modifier = Modifier.padding(top = 8.dp))
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
                Text(text = library.generateStateMessage(date, time))
            }
        }
    }
}