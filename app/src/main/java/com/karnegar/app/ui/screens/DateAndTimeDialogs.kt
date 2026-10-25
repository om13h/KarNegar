package com.karnegar.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.karnegar.app.util.PersianDateUtils

@Composable
fun TimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    onConfirm: (hour: Int, minute: Int) -> Unit,
    onDismiss: () -> Unit
) {
    val state = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true
    )
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("انتخاب ساعت") },
        text = { TimePicker(state = state) },
        confirmButton = {
            TextButton(onClick = { onConfirm(state.hour, state.minute) }) { Text("تأیید") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("لغو") }
        }
    )
}

@Composable
fun JalaliDatePickerDialog(
    initialMillis: Long,
    onConfirm: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val initial = PersianDateUtils.jalaliParts(initialMillis)
    val current = PersianDateUtils.currentJalali()

    var year by remember { mutableIntStateOf(initial.year) }
    var month by remember { mutableIntStateOf(initial.month) }
    var day by remember { mutableIntStateOf(initial.day) }

    val years = remember { (current.year - 5)..(current.year + 10) }
    val maxDay = PersianDateUtils.daysInMonth(year, month)
    val safeDay = day.coerceIn(1, maxDay)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("انتخاب تاریخ") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DropdownSelector(
                        modifier = Modifier.weight(1f),
                        label = PersianDateUtils.toPersianDigits(year.toString()),
                        options = years.map { it.toString() },
                        displayTransform = { PersianDateUtils.toPersianDigits(it) },
                        onSelect = { year = it.toInt() }
                    )
                    DropdownSelector(
                        modifier = Modifier.weight(2f),
                        label = PersianDateUtils.monthName(month),
                        options = PersianDateUtils.allMonthNames(),
                        displayTransform = { it },
                        onSelect = { month = PersianDateUtils.allMonthNames().indexOf(it) }
                    )
                    DropdownSelector(
                        modifier = Modifier.weight(1f),
                        label = PersianDateUtils.toPersianDigits(safeDay.toString()),
                        options = (1..maxDay).map { it.toString() },
                        displayTransform = { PersianDateUtils.toPersianDigits(it) },
                        onSelect = { day = it.toInt() }
                    )
                }
                val preview = PersianDateUtils.withJalaliDate(initialMillis, year, month, safeDay)
                Text(
                    "تاریخ انتخابی: " + PersianDateUtils.friendlyDate(preview),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(PersianDateUtils.withJalaliDate(initialMillis, year, month, safeDay))
                }
            ) { Text("تأیید") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("لغو") } }
    )
}

@Composable
fun DropdownSelector(
    modifier: Modifier,
    label: String,
    options: List<String>,
    displayTransform: (String) -> String,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
        ) {
            Text(label, maxLines = 1, style = MaterialTheme.typography.bodySmall)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(displayTransform(option)) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
