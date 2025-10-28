package com.devgardenaj.thisday.infra


import android.content.Context
import android.preference.PreferenceManager

fun flagPut(context: Context, value : Int)  {
    val preferences = PreferenceManager.getDefaultSharedPreferences(context)
    preferences.edit().putInt("edit_flag", value).apply()
}

fun flagGet(context: Context): Int {
    val preferences = PreferenceManager.getDefaultSharedPreferences(context)
    val editFlag = preferences.getInt("edit_flag", 0)
    return editFlag
}