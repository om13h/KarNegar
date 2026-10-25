package com.karnegar.app.util

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

    // ==================== Jalaali calendar algorithm ====================
    // Pure-Kotlin port of the well-known "jalaali-js" algorithm.
    // (android.icu does NOT expose PersianCalendar in its public API, so we
    // implement the conversion ourselves — dependency free and deterministic.)

    private fun div(a: Int, b: Int): Int = a / b
    private fun mod(a: Int, b: Int): Int = a - a / b * b

    private val breaks = intArrayOf(
        -61, 9, 38, 199, 426, 686, 756, 818, 1111, 1181, 1210,
        1635, 2060, 2097, 2192, 2262, 2324, 2394, 2456, 3178
    )

    private class JalCal(val leap: Int, val gy: Int, val march: Int)

    private fun jalCal(jy: Int): JalCal {
        var leapJ = -14
        var jp = breaks[0]
        var jump = 0
        val gy = jy + 621
        for (i in 1 until breaks.size) {
            val jm = breaks[i]
            jump = jm - jp
            if (jy < jm) break
            leapJ += div(jump, 33) * 8 + div(mod(jump, 33), 4)
            jp = jm
        }
        var n = jy - jp
        leapJ += div(n, 33) * 8 + div(mod(n, 33) + 3, 4)
        if (mod(jump, 33) == 4 && jump - n == 4) leapJ += 1
        val leapG = div(gy, 4) - div((div(gy, 100) + 1) * 3, 4) - 150
        val march = 20 + leapJ - leapG
        if (jump - n < 6) n = n - jump + div(jump + 4, 33) * 33
        var leap = mod(mod(n + 1, 33) - 1, 4)
        if (leap == -1) leap = 4
        return JalCal(leap, gy, march)
    }

    /** Gregorian date (1-based month) to Julian Day Number. */
    private fun g2d(gy: Int, gm: Int, gd: Int): Int {
        var d = div((gy + div(gm - 8, 6) + 100100) * 1461, 4) +
                div(153 * mod(gm + 9, 12) + 2, 5) + gd - 34840408
        d = d - div(div(gy + 100100 + div(gm - 8, 6), 100) * 3, 4) + 752
        return d
    }

    /** Julian Day Number to Gregorian date (1-based month). */
    private fun d2g(jdn: Int): Triple<Int, Int, Int> {
        var j = 4 * jdn + 139361631
        j += div(div(4 * jdn + 183187720, 146097) * 3, 4) * 4 - 3908
        val i = div(mod(j, 1461), 4) * 5 + 308
        val gd = div(mod(i, 153), 5) + 1
        val gm = mod(div(i, 153), 12) + 1
        val gy = div(j, 1461) - 100100 + div(8 - gm, 6)
        return Triple(gy, gm, gd)
    }

    /** Jalaali date (1-based month) to Julian Day Number. */
    private fun j2d(jy: Int, jm: Int, jd: Int): Int {
        val r = jalCal(jy)
        return g2d(r.gy, 3, r.march) + (jm - 1) * 31 - div(jm, 7) * (jm - 7) + jd - 1
    }

    /** Julian Day Number to Jalaali date (1-based month). */
    private fun d2j(jdn: Int): Triple<Int, Int, Int> {
        val gy = d2g(jdn).first
        var jy = gy - 621
        val r = jalCal(jy)
        val jdn1f = g2d(r.gy, 3, r.march)
        var k = jdn - jdn1f
        if (k >= 0) {
            if (k <= 185) {
                return Triple(jy, 1 + div(k, 31), mod(k, 31) + 1)
            }
            k -= 186
        } else {
            jy -= 1
            k += 179
            if (r.leap == 1) k += 1
        }
        return Triple(jy, 7 + div(k, 30), mod(k, 30) + 1)
    }

    fun isLeapJalaliYear(jy: Int): Boolean = jalCal(jy).leap == 0

    private fun jalaaliMonthLength(jy: Int, jm1Based: Int): Int = when {
        jm1Based <= 6 -> 31
        jm1Based <= 11 -> 30
        isLeapJalaliYear(jy) -> 30
        else -> 29
    }

    // ==================== Public API (0-based month, Calendar-style) ====================

    fun jalaliParts(millis: Long): JalaliDate {
        val cal = Calendar.getInstance()
        cal.timeInMillis = millis
        val (jy, jm, jd) = d2j(
            g2d(
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.DAY_OF_MONTH)
            )
        )
        return JalaliDate(jy, jm - 1, jd)
    }

    fun currentJalali(): JalaliDate = jalaliParts(System.currentTimeMillis())

    fun fromJalali(year: Int, month: Int, day: Int, hour: Int, minute: Int): Long {
        val (gy, gm, gd) = d2g(j2d(year, month + 1, day))
        val cal = Calendar.getInstance()
        cal.clear()
        cal.set(gy, gm - 1, gd, hour, minute, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    fun daysInMonth(year: Int, month: Int): Int = jalaaliMonthLength(year, month + 1)

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
        val tomorrow = jalaliParts(System.currentTimeMillis() + 24L * 60 * 60 * 1000)
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
