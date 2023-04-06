package com.allybros.elephant_todo_app.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.allybros.elephant_todo_app.db.Item
import java.util.*


/**
 * Created by orcun on 29.03.2023
 */

fun Context.setupNotification(it: Item) {
    val alarmMgr = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val alarmIntent: PendingIntent = Intent(this, AlarmReceiver::class.java).apply {
        putExtra(INTENT_EXTRA_TITLE, TITLE)
        putExtra(INTENT_EXTRA_DESCRIPTION, it.note)
    }.let { intent ->
        if (Build.VERSION.SDK_INT > 30)
            it.uid.let { uid-> PendingIntent.getBroadcast(this, uid!!, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE) }
        else
            it.uid.let { uid-> PendingIntent.getBroadcast(this, uid!!, intent, 0) }
    }

    if (it.hasTime == true && it.isComplete == false){
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            val hour = it.time?.split(":")?.get(0)?.toInt()
            val minutes = it.time?.split(":")?.get(1)?.toInt()
            if (hour != null) {
                set(Calendar.HOUR_OF_DAY, hour)
            }
            if (minutes != null) {
                set(Calendar.MINUTE, minutes)
            }
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (calendar.timeInMillis >= Calendar.getInstance().timeInMillis){
            alarmMgr.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                alarmIntent
            )
        }
    } else {
        alarmMgr.cancel(alarmIntent)
    }
}