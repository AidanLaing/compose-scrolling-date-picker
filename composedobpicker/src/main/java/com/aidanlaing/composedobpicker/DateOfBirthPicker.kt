package com.aidanlaing.composedobpicker

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

// TODO rename to DateScrollPicker
// TODO reduce gradle dependencies, min sdk
// TODO integration testing with CI
// TODO README
// TODO Maven publishing
// TODO Android dev post
@Composable
fun DateOfBirthPicker(
    dateOfBirthPickerUi: DateOfBirthPickerUi,
    maxYear: Int,
    dateOfBirthChanged: (dateOfBirth: DateOfBirth) -> Unit,
    modifier: Modifier = Modifier,
    properties: DateOfBirthPickerProperties = DateOfBirthPickerProperties()
) {
    var selectedDay: Int by rememberSaveable { mutableIntStateOf(properties.defaultSelectedDay) }
    var selectedMonth: Int by rememberSaveable { mutableIntStateOf(properties.defaultSelectedMonth) }
    var selectedYear: Int by rememberSaveable { mutableIntStateOf(properties.defaultSelectedYear) }
    var numDays: Int by rememberSaveable { mutableIntStateOf(calculateNumDaysInMonth(selectedMonth, selectedYear)) }

    val dateOfBirthElementList: ImmutableList<DateOfBirthElement> =
        remember { properties.dateOfBirthElementOrder.toList().toImmutableList() }

    val onGetDayText: (day: Int) -> String = remember { properties.getDayText }
    val onGetMonthText: (month: Int) -> String = remember { properties.getMonthText }
    val onGetYearText: (year: Int) -> String = remember { properties.getYearText }

    val onDaySelected: (newSelectedDay: Int) -> Unit = remember {
        { newSelectedDay ->
            val validatedDay = ensureValidDayNum(newSelectedDay, selectedMonth, selectedYear)
            selectedDay = validatedDay
            numDays = calculateNumDaysInMonth(selectedMonth, selectedYear)
            dateOfBirthChanged(DateOfBirth(validatedDay, selectedMonth, selectedYear))
        }
    }

    val onMonthSelected: (newSelectedMonth: Int) -> Unit = remember {
        { newSelectedMonth ->
            val validatedDay = ensureValidDayNum(selectedDay, newSelectedMonth, selectedYear)
            selectedDay = validatedDay
            selectedMonth = newSelectedMonth
            numDays = calculateNumDaysInMonth(newSelectedMonth, selectedYear)
            dateOfBirthChanged(DateOfBirth(validatedDay, newSelectedMonth, selectedYear))
        }
    }

    val onYearSelected: (newSelectedYear: Int) -> Unit = remember {
        { newSelectedYear ->
            val validatedDay = ensureValidDayNum(selectedDay, selectedMonth, newSelectedYear)
            selectedDay = validatedDay
            selectedYear = newSelectedYear
            numDays = calculateNumDaysInMonth(selectedMonth, newSelectedYear)
            dateOfBirthChanged(DateOfBirth(validatedDay, selectedMonth, newSelectedYear))
        }
    }

    DateElementRow(
        dateOfBirthElementList = dateOfBirthElementList,
        itemHeightDp = properties.itemHeightDp,
        numberOfDisplayedItems = properties.numberOfDisplayedItems,
        numDays = numDays,
        minYear = properties.minYear,
        maxYear = maxYear,
        defaultSelectedDay = properties.defaultSelectedDay,
        defaultSelectedMonth = properties.defaultSelectedMonth,
        defaultSelectedYear = properties.defaultSelectedYear,
        getDayText = onGetDayText,
        getMonthText = onGetMonthText,
        getYearText = onGetYearText,
        dayListItem = dateOfBirthPickerUi.determineDayListItem(),
        monthListItem = dateOfBirthPickerUi.determineMonthListItem(),
        yearListItem = dateOfBirthPickerUi.determineYearListItem(),
        daySelectedItemBackground = dateOfBirthPickerUi.determineDaySelectedItemBackground(),
        monthSelectedItemBackground = dateOfBirthPickerUi.determineMonthSelectedItemBackground(),
        yearSelectedItemBackground = dateOfBirthPickerUi.determineYearSelectedItemBackground(),
        onDaySelected = onDaySelected,
        onMonthSelected = onMonthSelected,
        onYearSelected = onYearSelected,
        modifier = modifier
    )
}

@Composable
private fun DateElementRow(
    dateOfBirthElementList: ImmutableList<DateOfBirthElement>,
    itemHeightDp: Dp,
    numberOfDisplayedItems: Int,
    numDays: Int,
    minYear: Int,
    maxYear: Int,
    defaultSelectedDay: Int,
    defaultSelectedMonth: Int,
    defaultSelectedYear: Int,
    getDayText: (day: Int) -> String,
    getMonthText: (month: Int) -> String,
    getYearText: (year: Int) -> String,
    dayListItem: @Composable LazyItemScope.(text: String, heightDp: Dp, isSelected: Boolean) -> Unit,
    monthListItem: @Composable LazyItemScope.(text: String, heightDp: Dp, isSelected: Boolean) -> Unit,
    yearListItem: @Composable LazyItemScope.(text: String, heightDp: Dp, isSelected: Boolean) -> Unit,
    daySelectedItemBackground: @Composable BoxScope.(heightDp: Dp, paddingTopDp: Dp) -> Unit,
    monthSelectedItemBackground: @Composable BoxScope.(heightDp: Dp, paddingTopDp: Dp) -> Unit,
    yearSelectedItemBackground: @Composable BoxScope.(heightDp: Dp, paddingTopDp: Dp) -> Unit,
    onDaySelected: (Int) -> Unit,
    onMonthSelected: (Int) -> Unit,
    onYearSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        dateOfBirthElementList.forEach { dateElement ->
            when (dateElement) {
                DateOfBirthElement.Day -> {
                    val dayItems: ImmutableList<Int> = (1..numDays).toImmutableList()
                    ScrollSelectionList(
                        items = dayItems,
                        itemHeightDp = itemHeightDp,
                        defaultSelectedItem = defaultSelectedDay,
                        numberOfDisplayedItems = numberOfDisplayedItems,
                        getItemText = getDayText,
                        selectedItemBackground = daySelectedItemBackground,
                        listItem = dayListItem,
                        onItemSelected = onDaySelected,
                        modifier = Modifier.weight(1f),
                        lazyColumnTestTag = "day_lazy_column_test_tag"
                    )
                }

                DateOfBirthElement.Month -> {
                    val monthItems: ImmutableList<Int> = (0..11).toImmutableList()
                    ScrollSelectionList(
                        items = monthItems,
                        itemHeightDp = itemHeightDp,
                        defaultSelectedItem = defaultSelectedMonth,
                        numberOfDisplayedItems = numberOfDisplayedItems,
                        getItemText = getMonthText,
                        selectedItemBackground = monthSelectedItemBackground,
                        listItem = monthListItem,
                        onItemSelected = onMonthSelected,
                        modifier = Modifier.weight(1f),
                        lazyColumnTestTag = "month_lazy_column_test_tag"
                    )
                }

                DateOfBirthElement.Year -> {
                    val yearItems: ImmutableList<Int> = (minYear..maxYear).toImmutableList()
                    ScrollSelectionList(
                        items = yearItems,
                        itemHeightDp = itemHeightDp,
                        defaultSelectedItem = defaultSelectedYear.coerceIn(minYear, maxYear),
                        numberOfDisplayedItems = numberOfDisplayedItems,
                        getItemText = getYearText,
                        selectedItemBackground = yearSelectedItemBackground,
                        listItem = yearListItem,
                        onItemSelected = onYearSelected,
                        modifier = Modifier.weight(1f),
                        lazyColumnTestTag = "year_lazy_column_test_tag"
                    )
                }
            }
        }
    }
}