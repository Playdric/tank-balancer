package com.cedric.tankbalancer.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)

val Typography.milSpec: Typography
    get() = this.copy(
        displayLarge = this.displayLarge.milSpecStyle(),
        displayMedium = this.displayMedium.milSpecStyle(),
        displaySmall = this.displaySmall.milSpecStyle(),
        headlineLarge = this.headlineLarge.milSpecStyle(),
        headlineMedium = this.headlineMedium.milSpecStyle(),
        headlineSmall = this.headlineSmall.milSpecStyle(),
        titleLarge = this.titleLarge.milSpecStyle(),
        titleMedium = this.titleMedium.milSpecStyle(),
        titleSmall = this.titleSmall.milSpecStyle(),
        bodyLarge = this.bodyLarge.milSpecStyle(),
        bodyMedium = this.bodyMedium.milSpecStyle(),
        bodySmall = this.bodySmall.milSpecStyle(),
        labelLarge = this.labelLarge.milSpecStyle(),
        labelMedium = this.labelMedium.milSpecStyle(),
        labelSmall = this.labelSmall.milSpecStyle()
    )

private fun TextStyle.milSpecStyle(): TextStyle {
    return this.copy(
        fontFamily = milSpecFamily,
        fontWeight = milSpecWeight
    )
}
