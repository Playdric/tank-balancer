package com.cedric.tankbalancer.presentation.composable

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.cedric.tankbalancer.presentation.theme.TankBalancerTheme
import com.cedric.tankbalancer.presentation.theme.milSpec

@Composable
fun CombinedFuelGauge(
    modifier: Modifier = Modifier,
    leftPercent: Double = 1.0,
    rightPercent: Double = 0.0,
    leftQuantity: String = "0",
    rightQuantity: String = "0",
    range: String = "00:00"
) {

    val maxRotation = 37F
    val minRotation = 142F
    val leftRotation = minRotation + (maxRotation - minRotation) * leftPercent.coerceIn(0.0, 1.0)
    val rightRotation = -(minRotation + (maxRotation - minRotation) * rightPercent.coerceIn(0.0, 1.0))

    Box(
        modifier = modifier
            .fillMaxHeight()
            .aspectRatio(1F)
            .clip(RoundedCornerShape(5.dp))
            .background(Color(0xFF919191))
            .padding(5.dp)
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(Color(0xFF282828))
                .padding(5.dp)
        ) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(Color(0xFF3D3D3D))
            ) {

                ArcWithGraduations(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 10.dp),
                    contentAlignment = Alignment.TopCenter

                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 48.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(text = "RNG", color = Color.White, style = MaterialTheme.typography.milSpec.bodySmall)
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            modifier = Modifier
                                .weight(1F)
                                .clip(RoundedCornerShape(5.dp))
                                .background(Color(0xFF151515))
                                .padding(3.dp),
                            text = range,
                            style = MaterialTheme.typography.milSpec.bodySmall,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }

                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 11.dp),
                    contentAlignment = Alignment.BottomCenter

                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 45.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            modifier = Modifier
                                .weight(1F)
                                .clip(RoundedCornerShape(5.dp))
                                .background(Color(0xFF151515))
                                .padding(3.dp),
                            text = leftQuantity,
                            style = MaterialTheme.typography.milSpec.bodySmall,
                            color = Color.White,
                            textAlign = TextAlign.Left
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            modifier = Modifier
                                .weight(1F)
                                .clip(RoundedCornerShape(5.dp))
                                .background(Color(0xFF151515))
                                .padding(3.dp),
                            text = rightQuantity,
                            style = MaterialTheme.typography.milSpec.bodySmall,
                            color = Color.White,
                            textAlign = TextAlign.Right
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val handWeight = 0.2F
                    val handSize = 125.dp
                    Box(
                        modifier = Modifier
                            .weight(handWeight)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        GaugeHand(handSize = handSize, rotation = leftRotation)
                    }
                    Spacer(modifier = Modifier.weight(1F))
                    Box(
                        modifier = Modifier
                            .weight(handWeight)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        GaugeHand(handSize = handSize, rotation = rightRotation)
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(.4F)
                            .fillMaxHeight()
                            .background(Color(0xFF000000)),
                        contentAlignment = Alignment.Center
                    ) {
                        VerticalText(
                            text = "LEFT"
                        )
                    }
                    Spacer(modifier = Modifier.weight(1F))
                    Box(
                        modifier = Modifier
                            .weight(.4F)
                            .fillMaxHeight()
                            .background(Color(0xFF000000)),
                        contentAlignment = Alignment.Center
                    ) {
                        VerticalText(
                            text = "RIGHT"
                        )
                    }
                }


            }
        }

    }
}

@Composable
private fun VerticalText(modifier: Modifier = Modifier, text: String) {
    Text(
        modifier = modifier,
        text = text.toCharArray().joinToString("\n"),
        color = Color.White,
        style = MaterialTheme.typography.milSpec.bodyMedium.merge(
            TextStyle(
                lineHeight = 1.2.em,
                platformStyle = PlatformTextStyle(
                    includeFontPadding = false
                ),
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Center,
                    trim = LineHeightStyle.Trim.None
                )
            )
        ),
        textAlign = TextAlign.Center,
    )
}

@Composable
fun ArcWithGraduations(modifier: Modifier) {
    Canvas(
        modifier = modifier
            .aspectRatio(1F)
    ) {
        val arcDegreesStart = 130F
        size.minDimension * 0.4f
        val arcSweepAngleDegrees = 100F
        val graduationCount = 5
        val centerX = size.width / 2F
        size.height / 2F
        val gradSize = 20F
        val gradRotations = (0 until graduationCount).map { index ->
            val angleParGraduation = arcSweepAngleDegrees / (graduationCount - 1)
            arcDegreesStart + (index * angleParGraduation) + 180F
        }
        val graduationPath = Path().apply {
            lineTo(gradSize, 0F)
        }
        translate(left = centerX + centerX * 0.15F) {
            drawArc(
                Color.White,
                startAngle = arcDegreesStart,
                sweepAngle = arcSweepAngleDegrees,
                useCenter = false,
                style = Stroke(width = 5F),
            )
            gradRotations.forEachIndexed { idx, angle ->
                rotate(angle) {
                    translate(top = size.height * 0.5F, left = gradSize * -0.5F) {
                        val color = if (idx == 0) Color.Red else Color.White
                        drawPath(path = graduationPath, color = color, style = Stroke(4F))
                    }
                }
            }
        }
        translate(left = -centerX - centerX * 0.15F) {
            scale(-1F, 1F) {
                drawArc(
                    Color.White,
                    startAngle = arcDegreesStart,
                    sweepAngle = arcSweepAngleDegrees,
                    useCenter = false,
                    style = Stroke(width = 5F),
                )
                gradRotations.forEachIndexed { idx, angle ->
                    rotate(angle) {
                        translate(top = size.height * 0.5F, left = gradSize * -0.5F) {
                            val color = if (idx == 0) Color.Red else Color.White
                            drawPath(path = graduationPath, color = color, style = Stroke(4F))
                        }
                    }
                }
            }
        }
    }

}

@Preview
@Composable
fun ArcWithGraduationsPreview(modifier: Modifier = Modifier) {
    TankBalancerTheme {
        Box(
            modifier = Modifier
                .size(70.dp)
                .background(Color.Gray),
            contentAlignment = Alignment.Center
        ) {
            ArcWithGraduations(Modifier)
        }
    }
}

@Composable
fun GaugeHand(handSize: Dp = 50.dp, rotation: Double = 0.0) {
    Canvas(
        modifier = Modifier
            .height(handSize)
            .width(handSize / 20)
    ) {

        val centerX = size.width / 2F
        size.height / 2F
        val handWhitePartHeight = size.height * 0.45F
        val handBlackPartHeight = size.height * 0.7F
        val handWhitePath = Path().apply {
            moveTo(centerX, 0F)
            lineTo(0F, size.height * 0.08F)
            lineTo(0F, handWhitePartHeight)
            lineTo(size.width, handWhitePartHeight)
            lineTo(size.width, size.height * 0.08F)
            lineTo(centerX, 0F)
        }
        val handBlackPath = Path().apply {
            moveTo(0F, handWhitePartHeight)
            lineTo(0F, handBlackPartHeight)
            lineTo(0F, handBlackPartHeight)
            lineTo(size.width, handBlackPartHeight)
            lineTo(size.width, handWhitePartHeight)
        }
        rotate(rotation.toFloat()) {
            drawPath(path = handWhitePath, color = Color(0xFF00FF00), style = Fill)
            drawPath(path = handBlackPath, color = Color.Black, style = Fill)
        }
    }
}


@Preview
@Composable
fun GaugeHandPreview(modifier: Modifier = Modifier) {
    TankBalancerTheme {
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(Color.Gray),
            contentAlignment = Alignment.Center
        ) {
            GaugeHand(rotation = 90.0)
        }
    }
}

@Preview
@Composable
fun CombinedFuelGaugePreview(modifier: Modifier = Modifier) {
    TankBalancerTheme {
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            CombinedFuelGauge(
                modifier = Modifier
                    .height(200.dp)
                    .width(200.dp),
            )
        }
    }
}
