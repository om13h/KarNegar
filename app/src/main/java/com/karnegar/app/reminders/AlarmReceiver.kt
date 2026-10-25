package com.karnegar.app.reminders

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.karnegar.app.data.AppDatabase
import com.karnegar.app.data.RepeatType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val result = goAsync()
        val action = intent.action ?: ACTION_REMIND
        val taskId = intent.getLongExtra(EXTRA_TASK_ID, -1L)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dao = AppDatabase.get(context).taskDao()
                val task = if (taskId > 0) dao.getById(taskId) else null
                if (task == null) return@launch

                when (action) {
                    ACTION_MARK_DONE -> {
                        dao.update(task.copy(isDone = true))
                        if (task.repeatEnum == RepeatType.NONE) {
                            ReminderScheduler.cancel(context, task.id)
                        }
                        NotificationHelper.cancel(context, task.id)
                    }

                    ACTION_SNOOZE -> {
                        val newTime = System.currentTimeMillis() + 5L * 60 * 1000
                        dao.update(task.copy(reminderAt = newTime, isDone = false))
                        ReminderScheduler.schedule(context, task.copy(reminderAt = newTime))
                        NotificationHelper.cancel(context, task.id)
                    }

                    else -> {
                        // Reminder fired
                        if (!task.isDone) {
                            NotificationHelper.show(context, task)
                        }
                        when (task.repeatEnum) {
                            RepeatType.DAILY -> {
                                val next = task.reminderAt + 24L * 60 * 60 * 1000
                                dao.update(task.copy(reminderAt = next, isDone = false))
                                ReminderScheduler.schedule(context, task.copy(reminderAt = next))
                            }
                            RepeatType.WEEKLY -> {
                                val next = task.reminderAt + 7L * 24 * 60 * 60 * 1000
                                dao.update(task.copy(reminderAt = next, isDone = false))
                                ReminderScheduler.schedule(context, task.copy(reminderAt = next))
                            }
                            RepeatType.NONE -> Unit
                        }
                    }
                }
            } catch (_: Exception) {
                // Never crash the system process
            } finally {
                result.finish()
            }
        }
    }

    companion object {
        const val ACTION_REMIND = "com.karnegar.app.ACTION_REMIND"
        const val ACTION_MARK_DONE = "com.karnegar.app.ACTION_MARK_DONE"
        const val ACTION_SNOOZE = "com.karnegar.app.ACTION_SNOOZE"
        const val EXTRA_TASK_ID = "extra_task_id"
    }
}
