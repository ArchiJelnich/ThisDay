package com.devgardenaj.thisday.infra

import android.content.Context
import androidx.core.content.ContextCompat.getString
import com.devgardenaj.thisday.R

fun inputChecker(context: Context, input: String): String {

    var newString = input

    if (newString.isEmpty())
    {
        newString=getString(context, R.string.category)
    }

    newString = newString.replace("*", "")

    if (newString.length>15)
    {
        newString = newString.take(15)
    }

    return newString
}