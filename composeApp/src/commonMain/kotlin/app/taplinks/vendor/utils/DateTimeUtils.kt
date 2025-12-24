package app.taplinks.vendor.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.DayOfWeekNames
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import kotlin.math.abs

object DateTimeUtils {

    @OptIn(FormatStringsInDatetimeFormats::class)
    fun formatAbsoluteDateTime(instant: Instant): String {
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        val format = LocalDateTime.Format {
            monthName(MonthNames.ENGLISH_ABBREVIATED)
            char(' ')
            dayOfMonth()
            chars(", ")
            year()
            chars(" at ")
            amPmHour()
            char(':')
            minute()
            char(' ')
            amPmMarker("AM", "PM")
        }
        return localDateTime.format(format)
    }

    fun formatRelativeTime(instant: Instant): String {
        val now = Clock.System.now()
        val duration = now - instant
        val seconds = duration.inWholeSeconds
        
        return when {
            seconds < 0 -> "Just now" 
            seconds < 60 -> "Just now"
            seconds < 3600 -> {
                val minutes = seconds / 60
                if (minutes == 1L) "1 minute ago" else "$minutes minutes ago"
            }
            seconds < 86400 -> {
                val hours = seconds / 3600
                if (hours == 1L) "1 hour ago" else "$hours hours ago"
            }
            seconds < 2592000 -> {
                val days = seconds / 86400
                if (days == 1L) "1 day ago" else "$days days ago"
            }
            seconds < 31536000 -> {
                val months = seconds / 2592000
                if (months == 1L) "1 month ago" else "$months months ago"
            }
            else -> {
                val years = seconds / 31536000
                if (years == 1L) "1 year ago" else "$years years ago"
            }
        }
    }

    @OptIn(FormatStringsInDatetimeFormats::class)
    fun formatShortDate(instant: Instant): String {
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        val format = LocalDateTime.Format {
            monthName(MonthNames.ENGLISH_ABBREVIATED)
            char(' ')
            dayOfMonth()
            chars(", ")
            year()
        }
        return localDateTime.format(format)
    }

    @OptIn(FormatStringsInDatetimeFormats::class)
    fun formatTime(instant: Instant): String {
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        val format = LocalDateTime.Format {
            amPmHour()
            char(':')
            minute()
            char(' ')
            amPmMarker("AM", "PM")
        }
        return localDateTime.format(format)
    }
}