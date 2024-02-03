package com.allybros.elephant.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.allybros.elephant.db.Item
import java.util.Calendar

/**
 * Created by orcun on 29.03.2023
 */

fun Context.setupNotification(item: Item) {
    val alarmMgr = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val alarmIntent: PendingIntent = Intent(this, AlarmReceiver::class.java).apply {
        putExtra(INTENT_EXTRA_TITLE, TITLE)
        putExtra(INTENT_EXTRA_DESCRIPTION, item.note)
    }.let { intent ->
        if (Build.VERSION.SDK_INT > 30)
            item.uid.let { uid-> PendingIntent.getBroadcast(this, uid!!, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE) }
        else
            item.uid.let { uid-> PendingIntent.getBroadcast(this, uid!!, intent, PendingIntent.FLAG_MUTABLE) }
    }

    if (item.hasTime == true && item.isComplete == false && item.date != null){
        if (item.date!! >= Calendar.getInstance().timeInMillis) {
            alarmMgr.setExact(
                AlarmManager.RTC_WAKEUP,
                item.date!!,
                alarmIntent
            )
        }
    } else {
        alarmMgr.cancel(alarmIntent)
    }
}