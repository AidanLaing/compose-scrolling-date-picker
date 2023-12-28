package com.aidanlaing.composedobpicker

internal fun calculateNumDaysInMonth(selectedMonth: Int, selectedYear: Int): Int =
    when (selectedMonth) {
        0, 2, 4, 6, 7, 9, 11 -> 31
        3, 5, 8, 10 -> 30
        1 -> if (calculateIsLeapYear(selectedYear)) 29 else 28
        else -> throw IndexOutOfBoundsException("Invalid Month index. Valid index range is 0 to 11")
    }

internal fun calculateIsLeapYear(selectedYear: Int): Boolean =
    when {
        selectedYear % 4 == 0 -> if (selectedYear % 100 == 0) selectedYear % 400 == 0 else true
        else -> false
    }

internal fun ensureValidDayNum(
    selectedDay: Int,
    selectedMonth: Int,
    selectedYear: Int
): Int {
    val numDaysInMonth = calculateNumDaysInMonth(selectedMonth, selectedYear)
    if (selectedDay > numDaysInMonth) return numDaysInMonth

    return selectedDay
}