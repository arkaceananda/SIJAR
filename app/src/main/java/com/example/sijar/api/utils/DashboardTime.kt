package com.example.sijar.api.utils

import com.example.sijar.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun greetingTime(): String {
    val now = LocalDateTime.now()
    val locale = Locale("id", "ID")

    return now.format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", locale))
}

fun greetingDay(): Int {
    val hour = LocalDateTime.now().hour
    return when (hour) {
        in 5..10 -> R.string.pagi
        in 11..14 -> R.string.siang
        in 15..18 -> R.string.sore
        else -> R.string.malam
    }
}