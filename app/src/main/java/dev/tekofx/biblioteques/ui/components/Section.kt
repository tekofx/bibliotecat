package dev.tekofx.biblioteques.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.tekofx.biblioteques.ui.IconResource

@Composable
fun Section(
    title: String,
    description: String,
    leftIcon: IconResource?,
    rightIcon: IconResource? = null,
    onClick: (() -> Unit)? = null,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 20.dp,
        shape = MaterialTheme.shapes.extraLarge,
        onClick = { onClick?.invoke() }
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 10.dp, horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                leftIcon?.let {
                    Icon(
                        modifier = Modifier.size(30.dp),
                        painter = leftIcon.asPainterResource(),
                        contentDescription = ""
                    )
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(title, style = MaterialTheme.typography.titleLarge)
                    Text(description)
                }
            }
            rightIcon?.let {
                Icon(
                    modifier = Modifier.size(30.dp),
                    painter = rightIcon.asPainterResource(),
                    contentDescription = ""
                )
            }
        }
    }
}

@Preview
@Composable
fun SectionPreview() {
    Section(
        "Title",
        "Description",
        leftIcon = IconResource.fromImageVector(Icons.Outlined.AccountCircle),
        rightIcon = IconResource.fromImageVector(Icons.Outlined.PlayArrow),
        onClick = {}
    )
}