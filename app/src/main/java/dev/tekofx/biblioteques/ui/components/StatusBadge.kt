package dev.tekofx.biblioteques.ui.components

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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import dev.tekofx.biblioteques.R
import dev.tekofx.biblioteques.model.StatusColor

@Composable
fun StatusBadge(
    statusColor: StatusColor,
    text: String,
    textStyle: TextStyle = TextStyle.Default
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(id = R.drawable.circle),
            contentDescription = null,
            modifier = Modifier
                .size(20.dp)
                .padding(end = 8.dp),
            colorFilter = ColorFilter.tint(colorResource(id = statusColor.value))
        )
        Text(
            text = text,
            style = textStyle
        )
    }
}