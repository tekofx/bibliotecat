package dev.tekofx.biblioteques.components.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.tekofx.biblioteques.ui.IconResource

@Composable
fun LibraryContactCard(iconResource: IconResource, text: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 20.dp,
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                iconResource.asPainterResource(),
                text,
                modifier = Modifier
                    .width(50.dp)
                    .height(50.dp)
            )
            Text(text = text, fontSize = 20.sp)
        }
    }
}