package com.example.sijar.api.utils

import com.example.sijar.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun greetingTime(): String {
    val now = LocalDateTime.now()
    val locale = Locale.getDefault()

    return now.format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", locale))
}

fun greetingDay(): Int {
    val hour = LocalDateTime.now().hour
    return when (hour) {
        in 5..10 -> R.string.greeting_morning
        in 11..14 -> R.string.greeting_afternoon
        in 15..18 -> R.string.greeting_evening
        else -> R.string.greeting_night
    }
}
