package com.karnegar.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TaskCategory(val label: String) {
    PERSONAL("شخصی"),
    WORK("کاری"),
    SHOPPING("خرید"),
    HEALTH("سلامت"),
    OTHER("سایر")
}

enum class TaskPriority(val label: String) {
    HIGH("بالا"),
    MEDIUM("متوسط"),
    LOW("پایین")
}

enum class RepeatType(val label: String) {
    NONE("بدون تکرار"),
    DAILY("هر روز"),
    WEEKLY("هر هفته")
}

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val category: String = TaskCategory.OTHER.name,
    val priority: String = TaskPriority.MEDIUM.name,
    val isDone: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val reminderAt: Long = 0,
    val repeatType: String = RepeatType.NONE.name
) {
    val categoryEnum: TaskCategory
        get() = runCatching { TaskCategory.valueOf(category) }.getOrDefault(TaskCategory.OTHER)

    val priorityEnum: TaskPriority
        get() = runCatching { TaskPriority.valueOf(priority) }.getOrDefault(TaskPriority.MEDIUM)

    val repeatEnum: RepeatType
        get() = runCatching { RepeatType.valueOf(repeatType) }.getOrDefault(RepeatType.NONE)
}
