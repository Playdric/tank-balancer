package com.cedric.tankbalancer.presentation.composable

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.cedric.domain.model.AircraftTank

@Composable
fun Aircraft(
    currentTank: AircraftTank?,
    currentTime: String,
    leftFuel: Double,
    rightFuel: Double,
    darkTheme: Boolean = false,
) {
    val durationTextMeasurer = rememberTextMeasurer()
    val leftFuelTextMeasurer = rememberTextMeasurer()
    val rightFuelTextMeasurer = rememberTextMeasurer()

    val style = TextStyle(
        fontSize = 30.sp,
        color = if (darkTheme) Color.White else Color.Black,
    )
    val durationText = currentTime
    val leftFuelText = "%.2f".format(leftFuel)
    val rightFuelText = "%.2f".format(rightFuel)
    val durationTextLayoutResult = remember(durationText) {
        durationTextMeasurer.measure(durationText, style)
    }
    val leftFuelTextLayoutResult = remember(leftFuelText) {
        leftFuelTextMeasurer.measure(leftFuelText, style)
    }
    val rightFuelTextLayoutResult = remember(rightFuelText) {
        rightFuelTextMeasurer.measure(rightFuelText, style)
    }

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(2f)
            .padding(5.dp)
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        val factor = canvasHeight / 60f

        val middleX = canvasWidth / 2f

        val cellPath = Path().apply {
            moveTo(middleX, 0f)
            arcTo(
                Rect(middleX - 5 * factor, 10f, middleX + 5 * factor, 10 * factor),
                180f,
                180f,
                true
            )
            lineTo(middleX + 5 * factor, 20 * factor)
            moveTo(middleX + 5 * factor, 35 * factor)
            lineTo(middleX + 5 * factor, 45 * factor)
            lineTo(middleX + 15 * factor, 50 * factor)
            lineTo(middleX + 15 * factor, 55 * factor)
            lineTo(middleX, 52 * factor)
            lineTo(middleX - 15 * factor, 55 * factor)
            lineTo(middleX - 15 * factor, 50 * factor)
            lineTo(middleX - 5 * factor, 45 * factor)
            lineTo(middleX - 5 * factor, 35 * factor)
            moveTo(middleX - 5 * factor, 20 * factor)
            lineTo(middleX - 5 * factor, 5 * factor)
        }

        val leftWingPath = Path().apply {
            moveTo(middleX - 5 * factor, 44 * factor)
            lineTo(0f, 35 * factor)
            lineTo(0f, 20 * factor)
            lineTo(middleX - 5 * factor, 12 * factor)
            close()
        }

        val rightWingPath = Path().apply {
            moveTo(middleX + 5 * factor, 12 * factor)
            lineTo(canvasWidth, 20 * factor)
            lineTo(canvasWidth, 35 * factor)
            lineTo(middleX + 5 * factor, 44 * factor)
            close()
        }

        val propellerPath = Path().apply {
            moveTo(middleX - 7 * factor, 0f)
            lineTo(middleX + 7 * factor, 0f)
        }

        drawPath(cellPath, color = if (darkTheme) Color.White else Color.Black, style = Stroke(width = factor))
        drawPath(propellerPath, color = if (darkTheme) Color.White else Color.Black, style = Stroke(width = factor))
        drawPath(leftWingPath, color = if (currentTank == AircraftTank.LEFT) Color.Green else Color.Red, style = Stroke(width = factor))
        drawPath(rightWingPath, color = if (currentTank == AircraftTank.RIGHT) Color.Green else Color.Red, style = Stroke(width = factor))

        val offset = when (currentTank) {
            AircraftTank.LEFT -> Offset(
                x = middleX / 2f - durationTextLayoutResult.size.width / 2,
                y = canvasHeight / 2f - durationTextLayoutResult.size.height - 10 * factor
            )
            AircraftTank.RIGHT -> Offset(
                x = middleX + middleX / 2f - durationTextLayoutResult.size.width / 2,
                y = canvasHeight / 2f - durationTextLayoutResult.size.height - 10 * factor
            )
            null -> return@Canvas
        }

        drawText(
            textMeasurer = durationTextMeasurer,
            text = durationText,
            style = style,
            topLeft = offset
        )

        drawText(
            textMeasurer = leftFuelTextMeasurer,
            text = leftFuelText,
            style = style,
            topLeft = Offset(
                x = middleX / 2f - leftFuelTextLayoutResult.size.width / 2,
                y = canvasHeight / 2f
            )
        )

        drawText(
            textMeasurer = rightFuelTextMeasurer,
            text = rightFuelText,
            style = style,
            topLeft = Offset(
                x = middleX + middleX / 2f - rightFuelTextLayoutResult.size.width / 2,
                y = canvasHeight / 2f
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AircraftPreview() {
    Box(modifier = Modifier.width(300.dp).height(230.dp)) {
        Aircraft(
            currentTank = AircraftTank.LEFT,
            currentTime = "10:00",
            leftFuel = 100.0,
            rightFuel = 100.0,
            darkTheme = false
        )
    }
}
