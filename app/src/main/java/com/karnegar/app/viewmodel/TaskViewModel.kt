package com.karnegar.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.karnegar.app.data.AppDatabase
import com.karnegar.app.data.RepeatType
import com.karnegar.app.data.SettingsRepository
import com.karnegar.app.data.Task
import com.karnegar.app.data.TaskCategory
import com.karnegar.app.data.TaskRepository
import com.karnegar.app.data.ThemeMode
import com.karnegar.app.reminders.ReminderScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class StatusFilter(val label: String) {
    ALL("همه"),
    ACTIVE("انجام‌نشده"),
    DONE("انجام‌شده")
}

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application
    private val repository = TaskRepository(AppDatabase.get(application).taskDao())

    val searchQuery = MutableStateFlow("")
    val statusFilter = MutableStateFlow(StatusFilter.ALL)
    val categoryFilter = MutableStateFlow<TaskCategory?>(null)

    val themeMode: StateFlow<Int> = SettingsRepository.themeFlow(context)
        .stateIn(viewModelScope, SharingStarted.Eagerly, ThemeMode.SYSTEM)

    data class UiState(
        val tasks: List<Task> = emptyList(),
        val total: Int = 0,
        val done: Int = 0
    )

    val uiState: StateFlow<UiState> = combine(
        repository.observeAll(),
        searchQuery,
        statusFilter,
        categoryFilter
    ) { tasks, query, status, category ->
        val q = query.trim()
        val filtered = tasks.filter { t ->
            val matchesStatus = when (status) {
                StatusFilter.ALL -> true
                StatusFilter.ACTIVE -> !t.isDone
                StatusFilter.DONE -> t.isDone
            }
            val matchesCategory = category == null || t.categoryEnum == category
            val matchesQuery = q.isEmpty() ||
                    t.title.contains(q, ignoreCase = true) ||
                    t.description.contains(q, ignoreCase = true)
            matchesStatus && matchesCategory && matchesQuery
        }
        UiState(tasks = filtered, total = tasks.size, done = tasks.count { it.isDone })
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState())

    init {
        // Re-schedule future reminders (covers reboot, clock changes, missed alarms)
        viewModelScope.launch(Dispatchers.IO) {
            runCatching { ReminderScheduler.rescheduleAll(context) }
        }
    }

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun setStatusFilter(filter: StatusFilter) {
        statusFilter.value = filter
    }

    fun setCategoryFilter(category: TaskCategory?) {
        categoryFilter.value = category
    }

    fun setThemeMode(mode: Int) {
        viewModelScope.launch { SettingsRepository.setThemeMode(context, mode) }
    }

    suspend fun getTask(id: Long): Task? = if (id <= 0) null else repository.getById(id)

    fun saveTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = repository.upsert(task)
            val saved = if (task.id == 0L) task.copy(id = id) else task
            if (saved.reminderAt > System.currentTimeMillis() && !saved.isDone) {
                ReminderScheduler.schedule(context, saved)
            } else if (saved.repeatEnum == RepeatType.NONE) {
                ReminderScheduler.cancel(context, saved.id)
            }
        }
    }

    fun toggleDone(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            val updated = task.copy(isDone = !task.isDone)
            repository.upsert(updated)
            if (updated.isDone) {
                if (updated.repeatEnum == RepeatType.NONE) {
                    ReminderScheduler.cancel(context, updated.id)
                }
            } else if (updated.reminderAt > System.currentTimeMillis()) {
                ReminderScheduler.schedule(context, updated)
            }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(task)
            ReminderScheduler.cancel(context, task.id)
        }
    }
}
