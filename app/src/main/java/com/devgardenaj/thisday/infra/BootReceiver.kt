package com.devgardenaj.thisday.infra

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.preference.PreferenceManager
import androidx.annotation.RequiresApi

class BootReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED) return

        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val notificationEnabled = preferences.getInt("setting_notification", 0) == 1
        if (!notificationEnabled) return

        val hour = preferences.getInt("notification_hour", 9)
        val minute = preferences.getInt("notification_minute", 0)
        AlarmHelper.setDailyAlarm(context, hour, minute)
    }
}
