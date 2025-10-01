package com.cedric.tankbalancer.presentation.composable

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun AirplaneView(
    modifier: Modifier = Modifier,
    cellColor: Color = Color(0xFF818b95),
    wingColor: Color = Color(0xFF4d5662),
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val viewSize = minOf(size.width, size.height)
        val centerX = size.width / 2F
        size.height / 2F

        val cellWidth = viewSize * 0.1F
        viewSize

        val ovalWidth = cellWidth * 1.4F
        val ovalHeight = viewSize * .3F

        val one = viewSize * 0.01F

        val cellPath = Path().apply {
            val cellAngle = viewSize * 0.01F
            val cellTopLeft = Offset(centerX - ovalWidth / 2F, ovalHeight / 2F)
            val cellTopRight = Offset(centerX + ovalWidth / 2F, ovalHeight / 2F)
            val cellBottomRight = Offset(centerX + ovalWidth / 2F - cellAngle, (viewSize / 3F) * 2)
            val cellBottomLeft = Offset(centerX - ovalWidth / 2F + cellAngle, (viewSize / 3F) * 2)

            moveTo(cellTopRight.x, cellTopRight.y)
            lineTo(cellBottomRight.x, cellBottomRight.y)
            cubicTo(
                x1 = cellBottomRight.x - one * 2.5F,
                y1 = cellBottomRight.y + one * 42F,
                x2 = cellBottomLeft.x + one * 2.5F,
                y2 = cellBottomLeft.y + one * 42F,
                x3 = cellBottomLeft.x,
                y3 = cellBottomLeft.y
            )
            lineTo(cellTopLeft.x, cellTopLeft.y)

            cubicTo(
                x1 = cellTopLeft.x + one * 0.01F,
                y1 = cellTopLeft.y - one * 19F,
                x2 = cellTopRight.x - one * 0.01F,
                y2 = cellTopRight.y - one * 19F,
                x3 = cellTopRight.x,
                y3 = cellTopRight.y
            )
        }
        val wingPath = Path().apply {

            moveTo(viewSize * 0.5F, viewSize * 0.25F)
            lineTo(viewSize * 0.01F, viewSize * 0.45F)
            lineTo(viewSize * 0.01F, viewSize * 0.55F)
            lineTo(viewSize * 0.5F, viewSize * 0.43F)
        }

//        drawPath(path = wingPath, color = Color.Red, style = Stroke(width = 3F))
        drawWithRoundedCorners(path = wingPath, radius = viewSize * 0.008F, pathColor = wingColor)
        drawWithRoundedCorners(
            path = wingPath.apply {
                transform(
                    Matrix().apply {
                        translate(viewSize, 0f)
                        scale(-1f, 1f)
                    }
                )
            },
            radius = viewSize * 0.008F,
            pathColor = wingColor
        )

        val tailPath = Path().apply {
            moveTo(viewSize * 0.5F, viewSize * 0.80F)
            lineTo(viewSize * 0.3F, viewSize * 0.90F)
            lineTo(viewSize * 0.3F, viewSize * 0.99F)
            lineTo(viewSize * 0.5F, viewSize * 0.95F)
        }
        drawWithRoundedCorners(path = tailPath, radius = viewSize * 0.008F, pathColor = wingColor)
        drawWithRoundedCorners(
            path = tailPath.apply {
                transform(
                    Matrix().apply {
                        translate(viewSize, 0f)
                        scale(-1f, 1f)
                    }
                )
            },
            radius = viewSize * 0.008F,
            pathColor = wingColor
        )
        drawPath(path = cellPath, color = cellColor, style = Fill)

    }
}

private fun DrawScope.drawWithRoundedCorners(path: Path, radius: Float, pathColor: Color) {
    drawIntoCanvas { canvas ->
        canvas.drawOutline(
            outline = Outline.Generic(path),
            paint = Paint().apply {
                color = pathColor
                pathEffect = PathEffect.cornerPathEffect(radius)
            }
        )
    }

}

@Preview(showBackground = true, backgroundColor = 0xFF1A1A1A)
@Composable
fun AirplaneViewPreview() {
    Box(
        modifier = Modifier
            .size(400.dp)
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        AirplaneView(
            modifier = Modifier
                .size(250.dp)
                .background(Color(0xFF1A1A1A)),
        )
    }
}
