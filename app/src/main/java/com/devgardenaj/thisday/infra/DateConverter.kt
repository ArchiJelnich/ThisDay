package com.devgardenaj.thisday.infra

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
fun dateToString(date: LocalDate): String {


    var month = date.monthValue.toString()

    if (date.monthValue<10)
    {
        month = "0" + date.monthValue.toString()
    }

    val stringDate = date.dayOfMonth.toString()+"."+month+"."+date.year.toString()
    return stringDate
}

@RequiresApi(Build.VERSION_CODES.O)
fun dateMYToString(date: LocalDate): String {


    var month = date.monthValue.toString()

    if (date.monthValue<10)
    {
        month = "0" + date.monthValue.toString()
    }

    val stringDate = month+"."+date.year.toString()
    return stringDate
}
