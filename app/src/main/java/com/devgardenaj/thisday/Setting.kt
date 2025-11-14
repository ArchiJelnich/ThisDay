package com.devgardenaj.thisday

import android.app.AlarmManager
import android.content.Intent
import android.icu.util.Calendar
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.devgardenaj.thisday.infra.AlarmHelper
import com.devgardenaj.thisday.infra.loadLanguage
import com.devgardenaj.thisday.infra.localeChecker
import com.devgardenaj.thisday.infra.saveLanguage
import com.devgardenaj.thisday.infra.setAppLocale
import com.devgardenaj.thisday.screens.BottomPanel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SettingsScreen() {




    var statsLocaleBool = true

    val preferences = PreferenceManager.getDefaultSharedPreferences(LocalContext.current)
    val settingNotificationFlag = preferences.getInt("setting_notification", 0)
    var statsThemeBool = false

    if (settingNotificationFlag==1)
    {
        statsThemeBool = true
    }


    var isNotification by remember { mutableStateOf(statsThemeBool) }
    val context = LocalContext.current
    val startLocale = loadLanguage(context)
    if (startLocale != "RU")
    {
        statsLocaleBool = false
    }


    MaterialTheme(
    ){
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ){
            Box(modifier = Modifier.fillMaxSize()){
                Column(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = stringResource(R.string.settings),
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier
                            .align(alignment = Alignment.CenterHorizontally)
                            .padding(top = 40.dp)
                            .padding(bottom = 40.dp),
                        textAlign = TextAlign.Center
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 20.dp)
                            .padding(start = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = stringResource(R.string.language))
                        Spacer(modifier = Modifier.weight(1f))
                        var isRussian by remember { mutableStateOf(statsLocaleBool) }
                        Switch(
                            checked = isRussian,
                            onCheckedChange = {
                                isRussian = it
                                var locale = "ENG"
                                if (isRussian) {
                                    locale = "RU"
                                }

                                setAppLocale(context, locale)
                                saveLanguage(context, locale)

                            }
                        )
                        Text(text = if (isRussian)
                        {
                            stringResource(R.string.ru)
                        }
                        else {
                            stringResource(R.string.eng)
                        },
                            modifier = Modifier.padding(start = 8.dp))
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 20.dp)
                            .padding(start = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = stringResource(R.string.notification))
                        Spacer(modifier = Modifier.weight(1f))
                        Switch(
                            checked = isNotification,
                            onCheckedChange = {
                                isNotification = it



                                if (isNotification){
                                    preferences.edit().putInt("setting_notification", 1).apply()
                                }
                                else{
                                    preferences.edit().putInt("setting_notification", 0).apply()
                                }


                            }
                        )
                        Text(text = if (isNotification) stringResource(R.string.on) else stringResource(R.string.off), modifier = Modifier.padding(start = 8.dp))
                    }

                    if (isNotification){
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 20.dp)
                                .padding(start = 20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            ShowPicker()
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 20.dp)
                            .padding(start = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                    }

                }



                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                ) {
                    BottomPanel()
                }

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowPicker(){

    val context = LocalContext.current
    val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    val currentTime = Calendar.getInstance()
    val timePickerState = rememberTimePickerState(
        initialHour = preferences.getInt("notification_hour", currentTime.get(Calendar.HOUR_OF_DAY)),
        initialMinute = preferences.getInt("notification_minute", currentTime.get(Calendar.MINUTE)),
        is24Hour = true,
    )

    LaunchedEffect(timePickerState.hour, timePickerState.minute) {
        preferences.edit()
            .putInt("notification_hour", timePickerState.hour)
            .putInt("notification_minute", timePickerState.minute)
            .apply()

    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        TimeInput(
            state = timePickerState
        )
    }

}

class SettingActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        localeChecker(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
          SettingsScreen()
        }

        if (Build.VERSION.SDK_INT >= 33) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                1
            )
        }

    }

    override fun onPause() {
        super.onPause()
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val notificationHour = preferences.getInt("notification_hour", 0)
        val notificationMinute = preferences.getInt("notification_minute", 0)



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(AlarmManager::class.java)
            if (alarmManager.canScheduleExactAlarms()) {
                AlarmHelper.setDailyAlarm(this, notificationHour, notificationMinute)
            } else {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            }
        } else {
            AlarmHelper.setDailyAlarm(this, notificationHour, notificationMinute)
        }
    }
}