package com.devgardenaj.thisday.screens

import android.Manifest
import android.app.AlarmManager
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import androidx.preference.PreferenceManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.devgardenaj.thisday.infra.AlarmHelper
import com.devgardenaj.thisday.infra.ThisDayTheme
import com.devgardenaj.thisday.infra.loadLanguage
import com.devgardenaj.thisday.infra.localeChecker
import com.devgardenaj.thisday.infra.saveLanguage
import com.devgardenaj.thisday.infra.setAppLocale
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.edit
import androidx.core.net.toUri
import com.devgardenaj.thisday.R

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    var statsLocaleBool = true

    val preferences = PreferenceManager.getDefaultSharedPreferences(LocalContext.current)
    val settingNotificationFlag = preferences.getInt("setting_notification", 0)
    var statsThemeBool = false

    if (settingNotificationFlag == 1) {
        statsThemeBool = true
    }

    var isNotification by remember { mutableStateOf(statsThemeBool) }
    val context = LocalContext.current
    val startLocale = loadLanguage(context)
    if (startLocale != "RU") {
        statsLocaleBool = false
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(R.string.settings),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                )
            )
        },
        bottomBar = { BottomPanel() }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            SettingRow {
                    Text(
                        text = stringResource(R.string.language),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    var isRussian by remember { mutableStateOf(statsLocaleBool) }
                    Switch(
                        checked = isRussian,
                        onCheckedChange = {
                            isRussian = it
                            val locale = if (isRussian) "RU" else "ENG"
                            setAppLocale(context, locale)
                            saveLanguage(context, locale)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                        )
                    )
                    Text(
                        text = if (isRussian) stringResource(R.string.ru) else stringResource(R.string.eng),
                        modifier = Modifier.padding(start = 8.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                SettingRow {
                    Text(
                        text = stringResource(R.string.notification),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Switch(
                        checked = isNotification,
                        onCheckedChange = {
                            isNotification = it
                            preferences.edit {
                                putInt("setting_notification", if (isNotification) 1 else 0)
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                        )
                    )
                    Text(
                        text = if (isNotification) stringResource(R.string.on) else stringResource(R.string.off),
                        modifier = Modifier.padding(start = 8.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

            if (isNotification) {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                SettingRow {
                    ShowPicker()
                }
            }
        }
    }
}

@Composable
private fun SettingRow(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}

private typealias RowScope = androidx.compose.foundation.layout.RowScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowPicker() {
    val context = LocalContext.current
    val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    val currentTime = Calendar.getInstance()
    val timePickerState = rememberTimePickerState(
        initialHour = preferences.getInt("notification_hour", currentTime.get(Calendar.HOUR_OF_DAY)),
        initialMinute = preferences.getInt("notification_minute", currentTime.get(Calendar.MINUTE)),
        is24Hour = true,
    )

    LaunchedEffect(timePickerState.hour, timePickerState.minute) {
        preferences.edit {
            putInt("notification_hour", timePickerState.hour)
                .putInt("notification_minute", timePickerState.minute)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        TimeInput(state = timePickerState)
    }
}

class SettingActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        localeChecker(this)
        super.onCreate(savedInstanceState)
        setContent {
            ThisDayTheme {
                SettingsScreen()
            }
        }

        if (Build.VERSION.SDK_INT >= 33) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
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
                intent.data = "package:$packageName".toUri()
                startActivity(intent)
            }
        } else {
            AlarmHelper.setDailyAlarm(this, notificationHour, notificationMinute)
        }
    }
}
