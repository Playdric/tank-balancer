package com.cedric.domain.formatter

import android.icu.text.DecimalFormat
import android.icu.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.floor
import kotlin.math.log10


/**
 * Formats a Double to a String with controlled precision and length.
 *
 * This function ensures:
 * 1. The total number of digits (integer + fraction) does not exceed `maxTotalDigits`.
 * 2. The number of decimal places does not exceed `maxDecimalPlaces`.
 * 3. The decimal separator is localized (e.g., ',' in French, '.' in English).
 * 4. The separator itself does not count towards the `maxTotalDigits` limit.
 *
 * @param maxTotalDigits The maximum total number of digits to display.
 * @param maxDecimalPlaces The absolute maximum number of decimal places to display, regardless of total digits.
 * @return The formatted string.
 */
fun Double.toStringWithMaxChar(maxTotalDigits: Int = Int.MAX_VALUE, maxDecimalPlaces: Int = Int.MAX_VALUE): String {
    val value = this
    if (!value.isFinite()) {
        return value.toString()
    }

    val integerDigits = if (value < 1.0 && value > -1.0) 1 else floor(log10(kotlin.math.abs(value))).toInt() + 1

    val allowedDecimalPlacesByTotal = (maxTotalDigits - integerDigits).coerceAtLeast(0)

    val finalDecimalPlaces = minOf(allowedDecimalPlacesByTotal, maxDecimalPlaces)

    val pattern = if (finalDecimalPlaces > 0) {
        "0." + "0".repeat(finalDecimalPlaces)
    } else {
        "0"
    }

    val symbols = DecimalFormatSymbols(Locale.getDefault())

    val formatter = DecimalFormat(pattern, symbols)

    return formatter.format(value)
}
