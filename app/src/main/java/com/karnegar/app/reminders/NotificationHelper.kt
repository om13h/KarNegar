package com.karnegar.app.reminders

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.karnegar.app.MainActivity
import com.karnegar.app.R
import com.karnegar.app.data.Task

object NotificationHelper {

    const val CHANNEL_ID = "reminders_channel"

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.channel_desc)
                enableVibration(true)
            }
            context.getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }

    private fun actionIntent(context: Context, action: String, taskId: Long): Intent =
        Intent(context, AlarmReceiver::class.java).apply {
            this.action = action
            putExtra(AlarmReceiver.EXTRA_TASK_ID, taskId)
        }

    fun show(context: Context, task: Task) {
        createChannel(context)

        val openIntent = PendingIntent.getActivity(
            context,
            task.id.toInt(),
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val doneIntent = PendingIntent.getBroadcast(
            context,
            (task.id * 10 + 1).toInt(),
            actionIntent(context, AlarmReceiver.ACTION_MARK_DONE, task.id),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val snoozeIntent = PendingIntent.getBroadcast(
            context,
            (task.id * 10 + 2).toInt(),
            actionIntent(context, AlarmReceiver.ACTION_SNOOZE, task.id),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val description =
            if (task.description.isNotBlank()) task.description
            else context.getString(R.string.notification_title)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(task.title)
            .setContentText(description)
            .setStyle(NotificationCompat.BigTextStyle().bigText(description))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(openIntent)
            .addAction(0, context.getString(R.string.action_done), doneIntent)
            .addAction(0, context.getString(R.string.action_snooze), snoozeIntent)

        val manager = NotificationManagerCompat.from(context)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || manager.areNotificationsEnabled()) {
            try {
                manager.notify(task.id.toInt(), builder.build())
            } catch (_: SecurityException) {
                // Permission not granted; ignore silently
            }
        }
    }

    fun cancel(context: Context, taskId: Long) {
        NotificationManagerCompat.from(context).cancel(taskId.toInt())
    }
}
