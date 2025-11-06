package com.devgardenaj.thisday.room

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.Month


@RequiresApi(Build.VERSION_CODES.O)
fun DateToCustomDate(date: LocalDate) : CustomDate
{
    val year = date.year
    val day = date.dayOfMonth

    val month = when (date.month) {
        Month.JANUARY -> 1
        Month.FEBRUARY -> 2
        Month.MARCH -> 3
        Month.APRIL -> 4
        Month.MAY -> 5
        Month.JUNE -> 6
        Month.JULY -> 7
        Month.AUGUST -> 8
        Month.SEPTEMBER -> 9
        Month.OCTOBER -> 10
        Month.NOVEMBER -> 11
        Month.DECEMBER -> 12
        else -> 0
    }

    val newDate = CustomDate(day,month,year)
    return newDate
}


data class CustomDate(
    val day: Int,
    val month: Int,
    val year: Int
)