package dev.tekofx.bibliotecat.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.tekofx.bibliotecat.ui.IconResource

@Composable
fun Section(
    title: String,
    description: String,
    leftIcon: IconResource?,
    tint:Color= MaterialTheme.colorScheme.onSurface,
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


            leftIcon?.let {
                Icon(
                    modifier = Modifier.size(30.dp),
                    painter = leftIcon.asPainterResource(),
                    contentDescription = "",
                    tint = tint
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(title, style = MaterialTheme.typography.titleLarge)
                Text(text = description)
            }
            Spacer(modifier = Modifier.width(10.dp))
            rightIcon?.let {
                Icon(
                    modifier = Modifier.size(30.dp),
                    painter = rightIcon.asPainterResource(),
                    contentDescription = "",
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