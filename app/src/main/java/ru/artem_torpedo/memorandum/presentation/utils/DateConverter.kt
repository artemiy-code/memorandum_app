package ru.artem_torpedo.memorandum.presentation.utils

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

object DateConverter {
    val minute = TimeUnit.MINUTES.toMillis(1)
    val hour = TimeUnit.HOURS.toMillis(1)
    val day = TimeUnit.DAYS.toMillis(1)
    val week = TimeUnit.DAYS.toMillis(7)
    val formatter: DateFormat = SimpleDateFormat.getDateInstance(DateFormat.SHORT)

    fun currentDate(): String {
        return formatter.format(System.currentTimeMillis())
    }

    fun convertDate(timestamp: Long): String {
        val diff = System.currentTimeMillis() - timestamp
        return when {
            diff < minute -> "Just now"
            diff < hour -> "${TimeUnit.MILLISECONDS.toMinutes(diff)} m."
            diff < day -> "${TimeUnit.MILLISECONDS.toHours(diff)} h."
            diff < week -> "${TimeUnit.MILLISECONDS.toDays(diff)} days"
            else -> formatter.format(timestamp)
        }
    }
}
