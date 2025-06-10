package dev.tekofx.bibliotecat.ui.components.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.tekofx.bibliotecat.R
import dev.tekofx.bibliotecat.model.library.Library
import dev.tekofx.bibliotecat.ui.components.StatusBadge
import dev.tekofx.bibliotecat.ui.theme.Typography

@Composable
fun LibraryCard(
    onClick: () -> Unit,
    library: Library
) {
    Surface(
        tonalElevation = 40.dp,
        shape = RoundedCornerShape(20.dp),
        onClick = onClick
    ) {

        Row(
            modifier = Modifier
                .height(150.dp)
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AsyncImage(
                model = library.image,
                placeholder = painterResource(R.drawable.local_library),
                contentDescription = null,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .aspectRatio(1f)
                    .fillMaxHeight(),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(2f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                Column {
                    Text(
                        text = library.name,
                        style = Typography.titleLarge
                    )
                    Text(
                        text = library.municipality,
                        style = Typography.titleMedium,
                    )
                }
                StatusBadge(
                    library.libraryStatus.color,
                    library.libraryStatus.message,
                    Typography.bodyMedium
                )

            }
        }
    }

}
