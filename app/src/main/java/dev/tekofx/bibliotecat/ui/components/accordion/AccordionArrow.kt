package dev.tekofx.bibliotecat.ui.components.accordion

import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate

@Composable
fun AccordionArrow(
    rotate: Boolean
) {
    var degree by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(rotate) {
        degree = if (rotate) {
            180f
        } else {
            0f
        }
    }

    val angle: Float by animateFloatAsState(
        targetValue = degree,
        animationSpec = SpringSpec(), label = "rotate"
    )

    Icon(
        modifier = Modifier.rotate(angle),
        imageVector = Icons.Outlined.KeyboardArrowDown,
        contentDescription = "accordionArrow"
    )
}