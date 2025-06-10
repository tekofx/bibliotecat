package dev.tekofx.bibliotecat.ui.screens.library


import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import dev.tekofx.bibliotecat.R
import dev.tekofx.bibliotecat.model.library.Library
import dev.tekofx.bibliotecat.model.library.TimeInterval
import dev.tekofx.bibliotecat.ui.IconResource
import dev.tekofx.bibliotecat.ui.components.InfoIntentCard
import dev.tekofx.bibliotecat.ui.components.StatusBadge
import dev.tekofx.bibliotecat.ui.components.accordion.AccordionAlt
import dev.tekofx.bibliotecat.ui.components.feedback.Loader
import dev.tekofx.bibliotecat.ui.theme.Typography
import dev.tekofx.bibliotecat.ui.viewModels.library.LibraryViewModel
import dev.tekofx.bibliotecat.utils.IntentType
import dev.tekofx.bibliotecat.utils.formatDayOfWeek
import java.time.LocalDate


@Composable
fun LibraryScreen(
    pointID: String?,
    libraryUrl: String?,
    libraryViewModel: LibraryViewModel,
) {
    Log.d("LibraryScreen", "Navigated to $pointID")

    // Data
    val currentLibrary by libraryViewModel.currentLibrary.collectAsState()

    // Loader
    val isLoading by libraryViewModel.isLoading.collectAsState()

    LaunchedEffect(key1 = Unit) {
        Log.d("LibraryScreen", "pointId $pointID libraryUrl $libraryUrl")
        libraryViewModel.getLibrary(pointID, libraryUrl)
    }

    if (currentLibrary == null || isLoading) {
        Loader(isLoading, "Obtenint Data")

    } else {
        currentLibrary?.let { library ->
            Column(
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current).data(library.image)
                        .memoryCachePolicy(CachePolicy.ENABLED).crossfade(true).build(),
                    placeholder = painterResource(R.drawable.local_library),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(2F)
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop
                )
                Column(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .padding(top = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(text = library.name, style = Typography.headlineSmall)
                    Text(text = library.municipality, style = Typography.titleLarge)
                    LibraryTest(library)
                    InfoIntentCard(IntentType.LOCATION, library.address)
                    library.emails?.forEach {
                        InfoIntentCard(
                            contactType = IntentType.MAIL, text = it
                        )
                    }

                    library.phones?.forEach {
                        InfoIntentCard(
                            contactType = IntentType.PHONE, text = it
                        )
                    }

                    library.webUrl?.let {
                        InfoIntentCard(
                            IntentType.WEB, library.webUrl
                        )
                    }

                }
            }
        }

    }

}


@Composable
fun LibraryTest(library: Library) {

    val nextSevenDaysTimetables = library.timetable?.getNextSevenDaysDayTimetables(LocalDate.now())

    AccordionAlt(header = {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                IconResource.fromDrawableResource(R.drawable.clock).asPainterResource(),
                contentDescription = ""
            )
            StatusBadge(
                library.libraryStatus.color,
                library.libraryStatus.message,
                Typography.titleMedium
            )
        }
    }, content = {

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(10.dp)
        ) {
            nextSevenDaysTimetables?.forEach {
                Surface(
                    tonalElevation = if (it.key == LocalDate.now()) 100.dp else if (it.value.open) 5.dp else 1.dp,
                    shape = MaterialTheme.shapes.small,
                ) {
                    Row(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formatDayOfWeek(it.key.dayOfWeek),
                            style = Typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {

                            if (it.value.timeIntervals.isEmpty()) {
                                Text("Tancat")
                            } else if (it.value.holiday != null) {
                                Text(it.value.holiday!!.name)
                            } else {
                                it.value.timeIntervals.forEach { timeInterval: TimeInterval ->
                                    Text(
                                        text = timeInterval.toString(),
                                        style = Typography.bodyLarge
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

    })
}

