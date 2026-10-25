package com.karnegar.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.karnegar.app.data.RepeatType
import com.karnegar.app.data.Task
import com.karnegar.app.data.TaskCategory
import com.karnegar.app.data.TaskPriority
import com.karnegar.app.ui.theme.CategoryColors
import com.karnegar.app.ui.theme.PriorityColors
import com.karnegar.app.util.PersianDateUtils
import com.karnegar.app.viewmodel.TaskViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun AddEditTaskScreen(
    viewModel: TaskViewModel,
    taskId: Long,
    onBack: () -> Unit
) {
    val isNew = taskId == 0L

    var loaded by rememberSaveable { mutableStateOf(isNew) }
    var loadedTask by remember { mutableStateOf<Task?>(null) }

    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf(TaskCategory.PERSONAL.name) }
    var priority by rememberSaveable { mutableStateOf(TaskPriority.MEDIUM.name) }
    var reminderEnabled by rememberSaveable { mutableStateOf(false) }
    var reminderAt by rememberSaveable { mutableStateOf(0L) }
    var repeatType by rememberSaveable { mutableStateOf(RepeatType.NONE.name) }
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var showTimePicker by rememberSaveable { mutableStateOf(false) }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(taskId) {
        if (taskId > 0) {
            viewModel.getTask(taskId)?.let { t ->
                loadedTask = t
                title = t.title
                description = t.description
                category = t.category
                priority = t.priority
                reminderEnabled = t.reminderAt > 0
                reminderAt = t.reminderAt
                repeatType = t.repeatType
            }
            loaded = true
        }
    }

    if (!loaded) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isNew) "کار جدید" else "ویرایش کار") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("عنوان کار *") },
                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("توضیحات (اختیاری)") },
                minLines = 3,
                shape = RoundedCornerShape(14.dp)
            )

            Text("دسته‌بندی", style = MaterialTheme.typography.titleSmall)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TaskCategory.entries.forEach { c ->
                    FilterChip(
                        selected = category == c.name,
                        onClick = { category = c.name },
                        label = { Text(c.label) },
                        leadingIcon = {
                            Box(
                                Modifier
                                    .size(8.dp)
                                    .background(CategoryColors[c] ?: androidx.compose.ui.graphics.Color.Gray, CircleShape)
                            )
                        }
                    )
                }
            }

            Text("اولویت", style = MaterialTheme.typography.titleSmall)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TaskPriority.entries.forEach { p ->
                    FilterChip(
                        selected = priority == p.name,
                        onClick = { priority = p.name },
                        label = { Text(p.label) },
                        leadingIcon = {
                            Box(
                                Modifier
                                    .size(8.dp)
                                    .background(PriorityColors[p] ?: androidx.compose.ui.graphics.Color.Gray, CircleShape)
                            )
                        }
                    )
                }
            }

            Card(shape = RoundedCornerShape(16.dp)) {
                Column(
                    Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                "یادآور",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                "در زمان مشخص نوتیفیکیشن بفرست",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = reminderEnabled,
                            onCheckedChange = { checked ->
                                reminderEnabled = checked
                                if (checked && reminderAt <= 0L) {
                                    // Default reminder: one hour from now
                                    reminderAt = System.currentTimeMillis() + 60L * 60 * 1000
                                }
                            }
                        )
                    }

                    AnimatedVisibility(visible = reminderEnabled) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedButton(
                                onClick = { showDatePicker = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    if (reminderAt > 0) PersianDateUtils.friendlyDate(reminderAt)
                                    else "انتخاب تاریخ"
                                )
                            }
                            OutlinedButton(
                                onClick = { showTimePicker = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    Icons.Default.AccessTime,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    if (reminderAt > 0) "ساعت: " + PersianDateUtils.formatTime(reminderAt)
                                    else "انتخاب ساعت"
                                )
                            }

                            Text("تکرار", style = MaterialTheme.typography.titleSmall)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                RepeatType.entries.forEach { r ->
                                    FilterChip(
                                        selected = repeatType == r.name,
                                        onClick = { repeatType = r.name },
                                        label = { Text(r.label) }
                                    )
                                }
                            }

                            if (reminderAt in 1..System.currentTimeMillis()) {
                                Text(
                                    "زمان یادآور باید در آینده باشد",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }

            Button(
                onClick = {
                    val base = loadedTask ?: Task(title = "")
                    viewModel.saveTask(
                        base.copy(
                            title = title.trim(),
                            description = description.trim(),
                            category = category,
                            priority = priority,
                            reminderAt = if (reminderEnabled) reminderAt else 0L,
                            repeatType = repeatType
                        )
                    )
                    onBack()
                },
                enabled = title.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    if (isNew) "افزودن کار" else "ذخیره تغییرات",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            if (!isNew) {
                OutlinedButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("حذف کار")
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }

    if (showDatePicker) {
        JalaliDatePickerDialog(
            initialMillis = if (reminderAt > 0) reminderAt else System.currentTimeMillis(),
            onConfirm = { millis ->
                reminderAt = millis
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }

    if (showTimePicker) {
        val cal = Calendar.getInstance()
        if (reminderAt > 0) cal.timeInMillis = reminderAt
        TimePickerDialog(
            initialHour = cal.get(Calendar.HOUR_OF_DAY),
            initialMinute = cal.get(Calendar.MINUTE),
            onConfirm = { hour, minute ->
                reminderAt = PersianDateUtils.combineDateTime(reminderAt, hour, minute)
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("حذف کار") },
            text = { Text("آیا از حذف «$title» مطمئن هستی؟ این عمل قابل بازگشت نیست.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        loadedTask?.let { viewModel.deleteTask(it) }
                        showDeleteDialog = false
                        onBack()
                    }
                ) { Text("حذف", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("لغو") }
            }
        )
    }
}
