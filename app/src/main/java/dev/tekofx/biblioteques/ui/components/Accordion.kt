package dev.tekofx.biblioteques.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.SpringSpec
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.tekofx.biblioteques.ui.IconResource
import dev.tekofx.biblioteques.ui.theme.Typography

@Composable
fun Accordion(
    title: String,
    description: String,
    icon: IconResource
) {

    var show by remember { mutableStateOf(false) }
    var rotateIcon by remember { mutableStateOf(false) }


    Card(
        onClick = {
            show = !show
            rotateIcon = !rotateIcon
        },
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier
                .animateContentSize(
                    animationSpec = SpringSpec()
                )
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        )
        {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icon.asPainterResource(), "")

                Text(
                    text = title,
                    style = Typography.titleLarge,
                    textAlign = TextAlign.Center
                )
                AccordionArrow(rotateIcon)


            }
            if (
                show
            ) {
                Text(
                    style = Typography.bodyLarge,
                    textAlign = TextAlign.Justify,
                    text = description,
                )
            }
        }
    }
}

