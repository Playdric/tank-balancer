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


    /**
     * Formats a duration given in decimal hours into a "HH:MM" string.     * @param hours The duration in hours (e.g., 2.5 for 2 hours and 30 minutes).
     * @return The formatted string "HH:MM".
     */
    fun formatHours(hours: Double): String {
        if (!hours.isFinite() || hours < 0) {
            return "--:--"
        }
        val totalMinutes = (hours * 60).toInt()
        val h = totalMinutes / 60
        val m = totalMinutes % 60
        return "%02d:%02d".format(h, m)
    }
}
