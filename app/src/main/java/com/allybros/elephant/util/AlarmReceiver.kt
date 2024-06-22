package com.allybros.elephant.util

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.allybros.elephant.R
import com.allybros.elephant.ui.screen.main.MainActivity
import com.allybros.elephant.ui.screen.main.MainRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.GregorianCalendar
import javax.inject.Inject

/**
 * Created by orcun on 25.02.2023
 */

@OptIn(DelicateCoroutinesApi::class)
@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {
    @Inject
    lateinit var repository: MainRepository

    override fun onReceive(context: Context?, p1: Intent?) {
        p1?.let {
            if (it.action.equals(Intent.ACTION_BOOT_COMPLETED)) {
                deviceBootCompletedActions(context)
            } else {
                handleAlarmData(context, p1)
            }
        }
    }

    private fun deviceBootCompletedActions(context: Context?) {
        GlobalScope.launch {
            repository.date = GregorianCalendar.getInstance().time.time
            repository.getAllNotes()
            repository.getAllNotes.collect { item ->
                item.forEach {
                    context?.setupNotification(it)
                }
            }
        }
    }

    private fun handleAlarmData(context: Context?, intent: Intent) {
        context?.let {
            val title = intent.getStringExtra(INTENT_EXTRA_TITLE)
            val description = intent.getStringExtra(INTENT_EXTRA_DESCRIPTION)

            createNotification(
                context = it,
                title = title ?: "",
                description = description ?: ""
            )

        }
    }

    @SuppressLint("MissingPermission")
    private fun createNotification(
        context: Context,
        title: String,
        description: String
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.elephant_icon)
            .setColor(context.resources.getColor(R.color.icon_background))
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    context.resources,
                    R.mipmap.ic_launcher_round
                )
            )
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(System.currentTimeMillis().toInt(), builder.build())
        }
    }
}