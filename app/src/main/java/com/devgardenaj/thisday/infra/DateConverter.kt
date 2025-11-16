package com.devgardenaj.thisday.infra

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
fun dateToString(date: LocalDate): String {


    var mounth = date.monthValue.toString()

    if (date.monthValue<10)
    {
        mounth = "0" + date.monthValue.toString()
    }

    val stringDate = date.dayOfMonth.toString()+"."+mounth+"."+date.year.toString()
    return stringDate
}
