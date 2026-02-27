package com.devgardenaj.thisday.widget


import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import com.devgardenaj.thisday.room.AppDatabase
import com.devgardenaj.thisday.R
import com.devgardenaj.thisday.infra.localeChecker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import androidx.core.graphics.toColorInt

class WidgetProvider : AppWidgetProvider() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray



    ) {

        CoroutineScope(Dispatchers.IO).launch {
            localeChecker(context)

            val safeContext =
                context.createDeviceProtectedStorageContext()

            val db = AppDatabase.getInstance(safeContext)
            val today = LocalDate.now()
            val info = db.InfoAboutDayDao().getInfoByDay(today.dayOfMonth, today.monthValue, today.year)
            val topID: Int = info
                .maxByOrNull { it.infoSum }
                ?.takeIf { it.infoSum > 0 }
                ?.categoryID
                ?: 0


            var topColor = "#FFFFFFFF"

            if (topID != 0)
            {
                topColor = db.CategoryDao().getColorByID(topID)
            }



            withContext(Dispatchers.Main) {
                for (widgetId in appWidgetIds) {
                    updateWidget(context, appWidgetManager, widgetId, topColor)
                }
            }
        }

    }

    companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        fun updateWidget(context: Context, appWidgetManager: AppWidgetManager, widgetId: Int, topcolor : String) {
            val remoteViews = RemoteViews(context.packageName, R.layout.widget_status)




            val colorInt = topcolor.toColorInt()


            remoteViews.setInt(
                R.id.statusCircle,
                "setColorFilter",
                colorInt
            )




            val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            remoteViews.setOnClickPendingIntent(R.id.statusCircle, pendingIntent)

            appWidgetManager.updateAppWidget(widgetId, remoteViews)
        }
    }




}

@RequiresApi(Build.VERSION_CODES.O)
fun forceWidgetUpdate(context: Context) {
    val intent = Intent(context, WidgetProvider::class.java).apply {
        action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
    }

    val manager = AppWidgetManager.getInstance(context)
    val ids = manager.getAppWidgetIds(
        ComponentName(context, WidgetProvider::class.java)
    )

    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
    context.sendBroadcast(intent)
}