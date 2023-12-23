package com.aidanlaing.composedobpicker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList

// TODO landscape mode / tablet
// TODO dialog / bottom sheet options?
// TODO Customization (Material theming support)?
// TODO reduce gradle dependencies, min sdk
// TODO integration testing with CI
// TODO README
// TODO Maven publishing
// TODO Android dev post
@Composable
fun DateOfBirthPicker(
    defaultListItem: @Composable LazyItemScope.(
        text: String,
        heightDp: Dp,
        isSelected: Boolean
    ) -> Unit,
    dateOfBirthChanged: (dateOfBirth: DateOfBirth) -> Unit,
    maxYear: Int,
    modifier: Modifier = Modifier,
    itemHeightDp: Dp = 56.dp,
    numberOfDisplayedItems: Int = 5,
    defaultSelectedDay: Int = 1,
    defaultSelectedMonth: Int = 0,
    defaultSelectedYear: Int = 2000,
    minYear: Int = 1900,
    dateOfBirthElementOrder: Triple<DateOfBirthElement, DateOfBirthElement, DateOfBirthElement> = Triple(
        DateOfBirthElement.Year,
        DateOfBirthElement.Month,
        DateOfBirthElement.Day
    ),
    monthNames: List<String> = Month.values().map { month -> month.name }.toPersistentList(),
    getDayText: (day: Int) -> String = { day -> day.toString() },
    getMonthText: (month: Int) -> String = { month -> monthNames[month] },
    getYearText: (year: Int) -> String = { year -> year.toString() },
    selectionBackground: @Composable BoxScope.(
        heightDp: Dp,
        paddingTopDp: Dp
    ) -> Unit = { heightDp, paddingTopDp ->
        Box(
            modifier = Modifier
                .padding(top = paddingTopDp)
                .height(heightDp)
                .fillMaxWidth()
                .background(Color.Gray.copy(alpha = 0.2f))
        )
    },
    dayListItem: @Composable LazyItemScope.(text: String, heightDp: Dp, isSelected: Boolean) -> Unit = defaultListItem,
    monthListItem: @Composable LazyItemScope.(text: String, heightDp: Dp, isSelected: Boolean) -> Unit = defaultListItem,
    yearListItem: @Composable LazyItemScope.(text: String, heightDp: Dp, isSelected: Boolean) -> Unit = defaultListItem
) {
    var selectedDay: Int by rememberSaveable { mutableIntStateOf(defaultSelectedDay) }
    var selectedMonth: Int by rememberSaveable { mutableIntStateOf(defaultSelectedMonth) }
    var selectedYear: Int by rememberSaveable { mutableIntStateOf(defaultSelectedYear) }

    val numDays: Int by remember {
        derivedStateOf {
            calculateNumDaysInMonth(selectedMonth, selectedYear)
        }
    }

    val dateOfBirthElementList: ImmutableList<DateOfBirthElement> =
        remember { dateOfBirthElementOrder.toList().toImmutableList() }

    val onGetDayText: (day: Int) -> String = remember { getDayText }
    val onGetMonthText: (month: Int) -> String = remember { getMonthText }
    val onGetYearText: (year: Int) -> String = remember { getYearText }

    val onDaySelected: (newSelectedDay: Int) -> Unit = remember {
        { newSelectedDay ->
            val validatedDay = ensureValidDayNum(newSelectedDay, selectedMonth, selectedYear)
            selectedDay = validatedDay
            dateOfBirthChanged(DateOfBirth(validatedDay, selectedMonth, selectedYear))
        }
    }

    val onMonthSelected: (newSelectedMonth: Int) -> Unit = remember {
        { newSelectedMonth ->
            val validatedDay = ensureValidDayNum(selectedDay, newSelectedMonth, selectedYear)
            selectedDay = validatedDay
            selectedMonth = newSelectedMonth
            dateOfBirthChanged(DateOfBirth(validatedDay, newSelectedMonth, selectedYear))
        }
    }

    val onYearSelected: (newSelectedYear: Int) -> Unit = remember {
        { newSelectedYear ->
            val validatedDay = ensureValidDayNum(selectedDay, selectedMonth, newSelectedYear)
            selectedDay = validatedDay
            selectedYear = newSelectedYear
            dateOfBirthChanged(DateOfBirth(validatedDay, selectedMonth, newSelectedYear))
        }
    }

    DateElementRow(
        dateOfBirthElementList = dateOfBirthElementList,
        itemHeightDp = itemHeightDp,
        numberOfDisplayedItems = numberOfDisplayedItems,
        numDays = numDays,
        minYear = minYear,
        maxYear = maxYear,
        defaultSelectedDay = defaultSelectedDay,
        defaultSelectedMonth = defaultSelectedMonth,
        defaultSelectedYear = defaultSelectedYear,
        getDayText = onGetDayText,
        getMonthText = onGetMonthText,
        getYearText = onGetYearText,
        dayListItem = dayListItem,
        monthListItem = monthListItem,
        yearListItem = yearListItem,
        selectionBackground = selectionBackground,
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
    selectionBackground: @Composable BoxScope.(heightDp: Dp, paddingTopDp: Dp) -> Unit,
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
                        selectionBackground = selectionBackground,
                        listItem = dayListItem,
                        onItemSelected = onDaySelected,
                        modifier = Modifier.weight(1f)
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
                        selectionBackground = selectionBackground,
                        listItem = monthListItem,
                        onItemSelected = onMonthSelected,
                        modifier = Modifier.weight(1f)
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
                        selectionBackground = selectionBackground,
                        listItem = yearListItem,
                        onItemSelected = onYearSelected,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}