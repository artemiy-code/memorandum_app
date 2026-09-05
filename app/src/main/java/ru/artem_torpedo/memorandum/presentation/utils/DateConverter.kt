package ru.artem_torpedo.memorandum.presentation.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ru.artem_torpedo.memorandum.R
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

    @Composable
    fun convertDate(timestamp: Long): String {
        val diff = System.currentTimeMillis() - timestamp
        return when {
            diff < minute -> stringResource(R.string.just_now)
            diff < hour -> stringResource(R.string.m, TimeUnit.MILLISECONDS.toMinutes(diff))
            diff < day -> stringResource(R.string.h, TimeUnit.MILLISECONDS.toHours(diff))
            diff < week -> stringResource(R.string.days, TimeUnit.MILLISECONDS.toDays(diff))
            else -> formatter.format(timestamp)
        }
    }
}
