package dev.tekofx.biblioteques.ui.components

import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.utsman.osmandcompose.MapProperties
import com.utsman.osmandcompose.ZoomButtonVisibility
import dev.tekofx.biblioteques.model.library.Library
import org.osmdroid.tileprovider.tilesource.TileSourceFactory

val noFullscreenProperties = MapProperties(
    isMultiTouchControls = false,
    isAnimating = false,
    isFlingEnable = false,
    isEnableRotationGesture = false,
    isTilesScaledToDpi = true,
    tileSources = TileSourceFactory.DEFAULT_TILE_SOURCE,
    zoomButtonVisibility = ZoomButtonVisibility.NEVER,
    minZoomLevel = 18.0,
    maxZoomLevel = 18.0,
)

@Composable
fun SmallMap(
    library: Library,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
    ) {
        Map(
            onClick = onClick,
            library = library,
            modifier = Modifier.pointerInput(Unit) {
                detectVerticalDragGestures { _, _ -> } // Disable Drag
            },
            properties = noFullscreenProperties,
        )
    }
}