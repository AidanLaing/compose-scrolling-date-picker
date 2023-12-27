package com.aidanlaing.composedobpicker

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.toPersistentList

@Immutable
data class DateOfBirthPickerProperties(
    val itemHeightDp: Dp = 56.dp,
    val numberOfDisplayedItems: Int = 5,
    val defaultSelectedDay: Int = 1,
    val defaultSelectedMonth: Int = 0,
    val defaultSelectedYear: Int = 2000,
    val minYear: Int = 1900,
    val dateOfBirthElementOrder: Triple<DateOfBirthElement, DateOfBirthElement, DateOfBirthElement> =
        Triple(
            DateOfBirthElement.Year,
            DateOfBirthElement.Month,
            DateOfBirthElement.Day
        ),
    val monthNames: List<String> = Month.values().map { month -> month.name }.toPersistentList(),
    val getDayText: (day: Int) -> String = { day -> day.toString() },
    val getMonthText: (month: Int) -> String = { month -> monthNames[month] },
    val getYearText: (year: Int) -> String = { year -> year.toString() },
)