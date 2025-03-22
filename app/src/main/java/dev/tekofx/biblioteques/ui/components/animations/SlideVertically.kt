package dev.tekofx.biblioteques.ui.components.animations

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable

enum class SlideDirection {
    UP, DOWN
}

@Composable
fun SlideVertically(
    visible: Boolean,
    direction: SlideDirection,
    content: @Composable () -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = {
            if (direction == SlideDirection.UP) {
                it / 2
            } else {
                -it / 2
            }
        }) + fadeIn(
            initialAlpha = 0.3f
        ),
        exit = slideOutVertically(targetOffsetY = {
            if (direction == SlideDirection.UP) {
                it / 2
            } else {
                -it / 2
            }
        }) + fadeOut(
            targetAlpha = 0.3f
        ),
    ) {
        content()
    }

}