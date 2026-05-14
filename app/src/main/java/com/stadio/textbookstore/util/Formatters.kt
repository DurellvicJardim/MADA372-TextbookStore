package com.stadio.textbookstore.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

//Format Unix timestamp for display: same day = HH:MM, yesterday = Yesterday, older = d MMM
fun formatTimestamp(timestamp: Long): String {
    val now = Calendar.getInstance()
    val then = Calendar.getInstance().apply { timeInMillis = timestamp }
    val sameYear = now.get(Calendar.YEAR) == then.get(Calendar.YEAR)
    val dayDiff = now.get(Calendar.DAY_OF_YEAR) - then.get(Calendar.DAY_OF_YEAR)

    return when {
        sameYear && dayDiff == 0 ->
            SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timestamp))
        sameYear && dayDiff == 1 -> "Yesterday"
        else -> SimpleDateFormat("d MMM", Locale.getDefault()).format(Date(timestamp))
    }
}

//Format Unix timestamp for inside message thread to always shows time
fun formatMessageTime(timestamp: Long): String {
    return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timestamp))
}