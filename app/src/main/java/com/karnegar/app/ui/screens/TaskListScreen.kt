package com.karnegar.app.ui.screens

import androidx.compose.animation.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.karnegar.app.data.Task
import com.karnegar.app.data.TaskCategory
import com.karnegar.app.ui.theme.CategoryColors
import com.karnegar.app.ui.theme.PriorityColors
import com.karnegar.app.util.PersianDateUtils
import com.karnegar.app.viewmodel.StatusFilter
import com.karnegar.app.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    viewModel: TaskViewModel,
    onAddTask: () -> Unit,
    onEditTask: (Long) -> Unit,
    onOpenSettings: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val search by viewModel.searchQuery.collectAsStateWithLifecycle()
    val status by viewModel.statusFilter.collectAsStateWithLifecycle()
    val category by viewModel.categoryFilter.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "کارنگار",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "تنظیمات")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddTask,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("کار جدید") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            ProgressCard(total = uiState.total, done = uiState.done)

            OutlinedTextField(
                value = search,
                onValueChange = viewModel::setSearchQuery,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                placeholder = { Text("جستجو در کارها...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (search.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(Icons.Default.Close, contentDescription = "پاک کردن")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )

            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusFilter.entries.forEach { filter ->
                    FilterChip(
                        selected = status == filter,
                        onClick = { viewModel.setStatusFilter(filter) },
                        label = { Text(filter.label) }
                    )
                }
            }

            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = category == null,
                    onClick = { viewModel.setCategoryFilter(null) },
                    label = { Text("همه دسته‌ها") }
                )
                TaskCategory.entries.forEach { c ->
                    FilterChip(
                        selected = category == c,
                        onClick = {
                            viewModel.setCategoryFilter(if (category == c) null else c)
                        },
                        label = { Text(c.label) },
                        leadingIcon = {
                            Box(
                                Modifier
                                    .size(8.dp)
                                    .background(CategoryColors[c] ?: Color.Gray, CircleShape)
                            )
                        }
                    )
                }
            }

            if (uiState.tasks.isEmpty()) {
                EmptyState(hasTasksButFiltered = uiState.total > 0)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 4.dp, bottom = 96.dp)
                ) {
                    items(uiState.tasks, key = { it.id }) { task ->
                        TaskCard(
                            task = task,
                            onToggleDone = { viewModel.toggleDone(task) },
                            onClick = { onEditTask(task.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProgressCard(total: Int, done: Int) {
    val progress = if (total == 0) 0f else done.toFloat() / total
    val animated by animateFloatAsState(targetValue = progress, label = "progress")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "پیشرفت کارها",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    PersianDateUtils.toPersianDigits("$done از $total"),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { animated },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp),
                strokeCap = StrokeCap.Round,
                trackColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.15f)
            )
            Spacer(Modifier.height(8.dp))
            val message = when {
                total == 0 -> "برای شروع، اولین کارت را اضافه کن"
                done == total -> "آفرین! همه کارها انجام شده است"
                else -> PersianDateUtils.toPersianDigits("${total - done} کار باقی مانده است")
            }
            Text(
                message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun TaskCard(task: Task, onToggleDone: () -> Unit, onClick: () -> Unit) {
    val categoryColor = CategoryColors[task.categoryEnum] ?: Color.Gray
    val priorityColor = PriorityColors[task.priorityEnum] ?: Color.Gray
    val contentAlpha = if (task.isDone) 0.5f else 1f
    val repeatSuffix = when (task.repeatEnum) {
        com.karnegar.app.data.RepeatType.DAILY -> " • هر روز"
        com.karnegar.app.data.RepeatType.WEEKLY -> " • هر هفته"
        com.karnegar.app.data.RepeatType.NONE -> ""
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(categoryColor)
            )
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(checked = task.isDone, onCheckedChange = { onToggleDone() })
                Column(Modifier.weight(1f)) {
                    Text(
                        task.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        textDecoration = if (task.isDone) TextDecoration.LineThrough else null,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = contentAlpha)
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            Modifier
                                .size(7.dp)
                                .background(categoryColor, CircleShape)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            task.categoryEnum.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = contentAlpha)
                        )
                        Spacer(Modifier.width(10.dp))
                        Box(
                            Modifier
                                .size(7.dp)
                                .background(priorityColor, CircleShape)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "اولویت ${task.priorityEnum.label}",
                            style = MaterialTheme.typography.labelSmall,
                            color = priorityColor.copy(alpha = contentAlpha)
                        )
                    }
                    if (task.reminderAt > 0) {
                        Spacer(Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Alarm,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                PersianDateUtils.friendlyDate(task.reminderAt) + repeatSuffix,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = contentAlpha)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyState(hasTasksButFiltered: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.FactCheck,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
            modifier = Modifier.size(72.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            if (hasTasksButFiltered) "نتیجه‌ای پیدا نشد" else "هنوز کاری ثبت نشده است",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            if (hasTasksButFiltered) "فیلترها یا عبارت جستجو را تغییر بده"
            else "با دکمه «کار جدید» اولین کار روزت را اضافه کن",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
