package com.cedric.tankbalancer.presentation.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun FuelGauge(
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    percentage: Float = 0.5F,
    time: String = "1:09:89",
    quantity: String = "78,12",
    cornerRadius: Dp = 10.dp,
    selectedOutlineColor: Color = Color(0xFF0088FF),
    selectedBackgroundColor: Color = Color(0xFF13161f),
    unselectedTextColor: Color = MaterialTheme.colorScheme.onBackground,
    selectedTextColor: Color = MaterialTheme.colorScheme.background,
    gaugeBackgroundColor: Color = Color(0xFF13161f),
    gaugeOutlineColor: Color = Color(0xFF39414c),
    fuelProgressColor: Color = Color(0xFF396f5f),
    fuelMarkColor: Color = Color(0xFF6e9e8f),

    ) {
    val outlineThickness = 3.dp
    val textColor = if (isSelected) selectedTextColor else unselectedTextColor

    Box(
        modifier = modifier
            .fillMaxHeight()
            .width(20.dp)
            .clip(RoundedCornerShape(cornerRadius))
            .run {
                if (isSelected) {
                    this
                        .background(selectedBackgroundColor)
                        .border(
                            width = outlineThickness,
                            color = selectedOutlineColor,
                            shape = RoundedCornerShape(cornerRadius)
                        )
                } else this
            }

    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            Spacer(modifier = Modifier.height(5.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                val txt = if (isSelected) time else ""
                Text(txt, color = textColor, style = MaterialTheme.typography.bodyLarge)
            }
            Spacer(modifier = Modifier.height(5.dp))
            Row(
                modifier = Modifier
                    .weight(1F)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                FuelIndicator(
                    backgroundColor = gaugeBackgroundColor,
                    outlineColor = gaugeOutlineColor,
                    fuelProgressColor = fuelProgressColor,
                    fuelMarkColor = fuelMarkColor,
                    percentage = percentage,
                )
            }
            Spacer(modifier = Modifier.height(5.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(quantity, color = textColor, style = MaterialTheme.typography.bodyLarge)
            }
            Spacer(modifier = Modifier.height(5.dp))
        }
    }
}

@Composable
fun FuelIndicator(
    modifier: Modifier = Modifier,
    percentage: Float = 0.55F,
    backgroundColor: Color,
    outlineColor: Color,
    fuelProgressColor: Color,
    fuelMarkColor: Color
) {
    val cornerRadius = 5.dp
    val outlineThickness = 2.dp
    Box(
        modifier = modifier
            .fillMaxHeight()
            .width(30.dp)
            .clip(RoundedCornerShape(cornerRadius))
            .background(backgroundColor)
            .border(
                width = outlineThickness,
                color = outlineColor,
                shape = RoundedCornerShape(cornerRadius)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
                .clip(RoundedCornerShape(2.dp))
        ) {
            Spacer(modifier = Modifier.weight(1F))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(percentage)
                    .background(fuelProgressColor)
            ) {

            }
        }


        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.weight(1F))
            Mark(fuelMarkColor)
            Spacer(modifier = Modifier.weight(1F))
            Mark(fuelMarkColor)
            Spacer(modifier = Modifier.weight(1F))
            Mark(fuelMarkColor)
            Spacer(modifier = Modifier.weight(1F))
            Mark(fuelMarkColor)
            Spacer(modifier = Modifier.weight(1F))

        }
    }
}

@Composable
private fun Mark(
    fuelMarkColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .width(17.dp)
                .height(3.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(fuelMarkColor)
        ) { }
    }
}

@Preview
@Composable
fun FuelGaugePreview(modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier
            .size(400.dp)
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        FuelGauge(
            modifier = Modifier
                .height(200.dp)
                .width(80.dp),
        )
    }
}
