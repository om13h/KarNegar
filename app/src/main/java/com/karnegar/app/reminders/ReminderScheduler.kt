package com.karnegar.app.reminders

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.karnegar.app.data.AppDatabase
import com.karnegar.app.data.Task

object ReminderScheduler {

    private fun pendingIntent(context: Context, taskId: Long): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = AlarmReceiver.ACTION_REMIND
            putExtra(AlarmReceiver.EXTRA_TASK_ID, taskId)
        }
        return PendingIntent.getBroadcast(
            context,
            taskId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    /** Schedules an exact alarm for the task reminder (skips if time is in the past). */
    fun schedule(context: Context, task: Task) {
        if (task.reminderAt <= System.currentTimeMillis()) return
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pi = pendingIntent(context, task.id)
        val canExact =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) am.canScheduleExactAlarms() else true
        if (canExact) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, task.reminderAt, pi)
        } else {
            // Fallback: inexact delivery if the user has not granted exact-alarm permission
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, task.reminderAt, pi)
        }
    }

    /** Cancels the scheduled alarm and any visible notification for this task. */
    fun cancel(context: Context, taskId: Long) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.cancel(pendingIntent(context, taskId))
        NotificationHelper.cancel(context, taskId)
    }

    /** Re-schedules every future reminder (used after reboot / app start). */
    suspend fun rescheduleAll(context: Context) {
        val dao = AppDatabase.get(context).taskDao()
        dao.getUpcoming(System.currentTimeMillis()).forEach { schedule(context, it) }
    }
}
