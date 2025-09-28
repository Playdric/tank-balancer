package com.cedric.domain.formatter

import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds

object TimeFormatter {
    fun formatElapsedTimeSince(since: Long, to: Long = System.currentTimeMillis()): String {
        val seconds = (to - since).milliseconds.inWholeSeconds
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        val s = seconds % 60

        return when {
            seconds == 0L -> "00:00"
            // hh:mm:ss
            h > 0 -> String.format(Locale.getDefault(), "%d:%02d:%02d", h, m, s)
            // mm:ss
            else -> String.format(Locale.getDefault(), "%02d:%02d", m, s)
        }
    }

    fun formatTimestamp(timestamp: Long): String {
        val seconds = timestamp.milliseconds.inWholeSeconds
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        val s = seconds % 60

        return when {
            seconds == 0L -> "00:00"
            // hh:mm:ss
            h > 0 -> String.format(Locale.getDefault(), "%d:%02d:%02d", h, m, s)
            // mm:ss
            else -> String.format(Locale.getDefault(), "%02d:%02d", m, s)
        }
    }
}
