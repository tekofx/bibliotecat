package dev.tekofx.biblioteques.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import dev.tekofx.biblioteques.R
import dev.tekofx.biblioteques.model.Library
import dev.tekofx.biblioteques.model.LibraryDummy
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

    Surface(tonalElevation = 40.dp, shape = RoundedCornerShape(20.dp)) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = library.imatge, // Ajusta con tu imagen
                contentDescription = null,
                modifier = Modifier
                    .size(130.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {

                    Text(
                        text = library.adrecaNom,
                        fontSize = 20.sp
                    )
                    Text(text = library.municipiNom, modifier = Modifier.padding(top = 8.dp))
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
        }
    }

}


@Preview
@Composable
fun LibraryCardPreview() {
    LibraryItem(LibraryDummy)
}