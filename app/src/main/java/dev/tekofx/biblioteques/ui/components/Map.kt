package dev.tekofx.biblioteques.ui.components

import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.utsman.osmandcompose.DefaultMapProperties
import com.utsman.osmandcompose.Marker
import com.utsman.osmandcompose.OpenStreetMap
import com.utsman.osmandcompose.ZoomButtonVisibility
import com.utsman.osmandcompose.rememberCameraState
import com.utsman.osmandcompose.rememberMarkerState
import dev.tekofx.biblioteques.R
import dev.tekofx.biblioteques.model.library.Library
import dev.tekofx.biblioteques.utils.IntentType
import dev.tekofx.biblioteques.utils.openApp
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint

@Composable
fun Map(library: Library) {
    val context = LocalContext.current

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

    // Map properties
    var mapProperties by remember {
        mutableStateOf(DefaultMapProperties)
    }
    SideEffect {
        mapProperties = mapProperties
            .copy(isMultiTouchControls = false)
            .copy(isAnimating = false)
            .copy(isFlingEnable = false)
            .copy(isEnableRotationGesture = false)
            .copy(isTilesScaledToDpi = true)
            .copy(tileSources = TileSourceFactory.DEFAULT_TILE_SOURCE)
            .copy(zoomButtonVisibility = ZoomButtonVisibility.NEVER)
            .copy(minZoomLevel = 18.0)
            .copy(maxZoomLevel = 18.0)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
    )
    {
        OpenStreetMap(
            cameraState = cameraState,
            properties = mapProperties,
            onMapClick = {
                openApp(
                    context,
                    IntentType.LOCATION,
                    "${library.location[0]},${library.location[1]}"
                )
            },
            modifier = Modifier
                .pointerInput(Unit) {
                    detectDragGestures { _, _ -> } // Disable Drag
                }
        ) {
            Marker(
                icon = markerIcon,
                state = markerState
            )
        }
    }
}
