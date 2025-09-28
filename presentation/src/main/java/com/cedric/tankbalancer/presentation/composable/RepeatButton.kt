package com.cedric.tankbalancer.presentation.composable

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.unit.dp
import com.cedric.tankbalancer.presentation.theme.spacing
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun RepeatIconButton(
    onRepeat: () -> Unit,
    onRelease: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector,
    enabled: Boolean = true,
) {
    var isPressed by remember { mutableStateOf(false) }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            var delayTime = 500L
            while (this.isActive && isPressed && enabled) {
                onRepeat()
                delay(delayTime)
                delayTime = (delayTime * DELAY_REDUCTION_FACTOR).coerceAtLeast(MIN_DELAY).toLong()
            }
        }
    }

    SquareIconButton(
        modifier = modifier.pointerInteropFilter {
            when (it.action) {
                android.view.MotionEvent.ACTION_DOWN -> {
                    isPressed = true
                    true
                }

                android.view.MotionEvent.ACTION_UP,
                android.view.MotionEvent.ACTION_CANCEL -> {
                    isPressed = false
                    onRelease()
                    true
                }

                else -> false
            }
        },
        enabled = enabled,
        onClick = {},
        icon = icon,
    )
}

@Composable
fun SquareIconButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    icon: ImageVector,
    enabled: Boolean = true
) {
    FilledTonalButton(
        modifier = modifier
            .size(75.dp)
            .aspectRatio(1F),
        onClick = onClick,
        shape = RoundedCornerShape(MaterialTheme.spacing.medium),
        enabled = enabled
    ) {
        Icon(imageVector = icon, null)
    }
}

private const val DELAY_REDUCTION_FACTOR = 0.7
private const val MIN_DELAY = 25.0
