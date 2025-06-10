package dev.tekofx.biblioteques.ui.components

import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.utsman.osmandcompose.DefaultMapProperties
import com.utsman.osmandcompose.MapProperties
import com.utsman.osmandcompose.Marker
import com.utsman.osmandcompose.OpenStreetMap
import com.utsman.osmandcompose.rememberCameraState
import com.utsman.osmandcompose.rememberMarkerState
import dev.tekofx.biblioteques.R
import dev.tekofx.biblioteques.model.library.Library
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint


@Composable
fun Map(
    modifier: Modifier = Modifier,
    library: Library,
    onClick: () -> Unit,
    properties: MapProperties = DefaultMapProperties,
) {
    val context = LocalContext.current

    // Config userAgent for OpenStreetMaps
    Configuration.getInstance().userAgentValue = context.getString(R.string.app_name)

    val cameraState = rememberCameraState {
        geoPoint = GeoPoint(
            library.location[0],
            library.location[1]
        )
        speed = 0L
        zoom = 18.0 // optional, default is 5.0
    }

    // Marker
    val markerState = rememberMarkerState(
        geoPoint = GeoPoint(
            library.location[0],
            library.location[1]
        )
    )

    val markerIcon: Drawable? by remember {
        mutableStateOf(
            AppCompatResources.getDrawable(
                context,
                R.drawable.local_library
            )
        )
    }



    OpenStreetMap(
        cameraState = cameraState,
        properties = properties,
        onMapClick = { onClick() },
        modifier = modifier
    ) {
        Marker(
            icon = markerIcon,
            state = markerState
        )
    }
}
