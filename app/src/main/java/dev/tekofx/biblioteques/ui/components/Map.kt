package dev.tekofx.biblioteques.ui.components

import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
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
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint

@Composable
fun Map(library: Library) {
    // define camera state
    val depokMarkerState = rememberMarkerState(
        geoPoint = GeoPoint(
            library.location[0],
            library.location[1]
        )
    )
    var mapProperties by remember {
        mutableStateOf(DefaultMapProperties)
    }
    SideEffect {
        mapProperties = mapProperties
            .copy(isTilesScaledToDpi = true)
            .copy(tileSources = TileSourceFactory.DEFAULT_TILE_SOURCE)
            .copy(isEnableRotationGesture = false)
            .copy(zoomButtonVisibility = ZoomButtonVisibility.NEVER)
            .copy(isMultiTouchControls = false)
            .copy(isFlingEnable = false)
            .copy(isAnimating = false)
    }
    val cameraState = rememberCameraState {
        geoPoint = GeoPoint(
            library.location[0],
            library.location[1]
        )
        zoom = 18.0 // optional, default is 5.0
    }

    val context = LocalContext.current
    // define marker icon
    val depokIcon: Drawable? by remember {
        mutableStateOf(
            AppCompatResources.getDrawable(
                context,
                R.drawable.local_library
            )
        )
    }

    // add node
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
    ) {
        OpenStreetMap(
            cameraState = cameraState,
            properties = mapProperties
        ) {
            Marker(
                icon = depokIcon,
                state = depokMarkerState // add marker state
            )
        }
    }
}
