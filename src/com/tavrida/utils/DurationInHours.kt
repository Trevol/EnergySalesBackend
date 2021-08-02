package com.tavrida.utils

import java.time.Duration
import java.time.LocalDateTime

@JvmName("durationHoursNullable")
inline fun durationHours(start: LocalDateTime?, end: LocalDateTime?): Double? {
    return durationHours(
        start ?: return null,
        end ?: return null
    )
}

private const val msInHour = 60 * 60 * 1000.0 //  minInHour * secsInMin * msInSec

fun durationHours(start: LocalDateTime, end: LocalDateTime) = Duration.between(start, end).toMillis() / msInHour