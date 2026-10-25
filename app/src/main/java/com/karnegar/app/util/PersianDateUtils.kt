package com.karnegar.app.util

import android.icu.util.PersianCalendar
import java.util.Calendar
import java.util.Locale

data class JalaliDate(val year: Int, val month: Int, val day: Int)

object PersianDateUtils {

    private val monthNames = listOf(
        "فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور",
        "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند"
    )

    fun toPersianDigits(input: String): String {
        val fa = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
        return input.map { if (it in '0'..'9') fa[it - '0'] else it }.joinToString("")
    }

    fun jalaliParts(millis: Long): JalaliDate {
        val pc = PersianCalendar()
        pc.timeInMillis = millis
        return JalaliDate(
            year = pc.get(PersianCalendar.YEAR),
            month = pc.get(PersianCalendar.MONTH),
            day = pc.get(PersianCalendar.DAY_OF_MONTH)
        )
    }

    fun currentJalali(): JalaliDate = jalaliParts(System.currentTimeMillis())

    fun fromJalali(year: Int, month: Int, day: Int, hour: Int, minute: Int): Long {
        val pc = PersianCalendar()
        pc.clear()
        pc.set(year, month, day, hour, minute, 0)
        pc.set(Calendar.MILLISECOND, 0)
        return pc.timeInMillis
    }

    fun daysInMonth(year: Int, month: Int): Int {
        val pc = PersianCalendar()
        pc.clear()
        pc.set(year, month, 1)
        return pc.getActualMaximum(PersianCalendar.DAY_OF_MONTH)
    }

    fun monthName(month: Int): String = monthNames[month]

    fun allMonthNames(): List<String> = monthNames

    fun formatTime(millis: Long): String {
        val cal = Calendar.getInstance()
        cal.timeInMillis = millis
        val h = String.format(Locale.US, "%02d", cal.get(Calendar.HOUR_OF_DAY))
        val m = String.format(Locale.US, "%02d", cal.get(Calendar.MINUTE))
        return toPersianDigits("$h:$m")
    }

    fun formatDateTime(millis: Long): String {
        if (millis <= 0) return ""
        val parts = jalaliParts(millis)
        val dateStr = toPersianDigits(
            String.format(Locale.US, "%04d/%02d/%02d", parts.year, parts.month + 1, parts.day)
        )
        return "$dateStr - ${formatTime(millis)}"
    }

    /** Shows «امروز» / «فردا» when possible, otherwise the full Jalali date + time. */
    fun friendlyDate(millis: Long): String {
        if (millis <= 0) return ""
        val parts = jalaliParts(millis)
        val today = currentJalali()
        val tomorrowCal = PersianCalendar()
        tomorrowCal.timeInMillis = System.currentTimeMillis() + 24L * 60 * 60 * 1000
        val tomorrow = JalaliDate(
            tomorrowCal.get(PersianCalendar.YEAR),
            tomorrowCal.get(PersianCalendar.MONTH),
            tomorrowCal.get(PersianCalendar.DAY_OF_MONTH)
        )
        val time = formatTime(millis)
        return when {
            parts == today -> "امروز $time"
            parts == tomorrow -> "فردا $time"
            else -> formatDateTime(millis)
        }
    }

    /** Combines a date (taken from [dateMillis], or today if 0) with a new hour/minute. */
    fun combineDateTime(dateMillis: Long, hour: Int, minute: Int): Long {
        val parts = if (dateMillis > 0) jalaliParts(dateMillis) else currentJalali()
        return fromJalali(parts.year, parts.month, parts.day, hour, minute)
    }

    /** Keeps the time-of-day of [initialMillis] but changes the Jalali date. */
    fun withJalaliDate(initialMillis: Long, year: Int, month: Int, day: Int): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = if (initialMillis > 0) initialMillis else System.currentTimeMillis()
        return fromJalali(year, month, day, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))
    }
}
