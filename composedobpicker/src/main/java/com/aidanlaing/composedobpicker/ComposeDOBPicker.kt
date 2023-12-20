package com.aidanlaing.composedobpicker

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.UiComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import java.util.Calendar

// TODO code cleanup and optimization
// TODO expose date in better format for onDateChanged
// TODO landscape mode / tablet
// TODO dialog / bottom sheet options?
// TODO Customization (Material theming support)?
// TODO reduce gradle dependencies, min sdk
// TODO integration testing with CI
// TODO README
// TODO Maven publishing
// TODO Android dev post
@Composable
fun ComposeDOBPicker(
    modifier: Modifier = Modifier,
    itemHeightDp: Dp = 64.dp,
    numberOfDisplayedItems: Int = 5,
    defaultSelectedDay: Int = 1,
    defaultSelectedMonth: Int = 0,
    defaultSelectedYear: Int = 2000,
    minYear: Int = 1900,
    maxYear: Int = Calendar.getInstance().get(Calendar.YEAR),
    dateElementOrder: Triple<DateElement, DateElement, DateElement> = Triple(
        DateElement.Year,
        DateElement.Month,
        DateElement.Day
    ),
    monthNames: List<String> = persistentListOf(
        "January",
        "February",
        "March",
        "April",
        "May",
        "June",
        "July",
        "August",
        "September",
        "October",
        "November",
        "December"
    ),
    getDayText: (day: Int) -> String = remember { { day -> day.toString() } },
    getMonthText: (month: Int) -> String = remember { { month -> monthNames[month] } },
    getYearText: (year: Int) -> String = remember { { year -> year.toString() } },
    selectionBackground: @Composable @UiComposable BoxScope.(
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
    defaultListItem: @Composable @UiComposable LazyItemScope.(
        text: String,
        heightDp: Dp,
        isSelected: Boolean
    ) -> Unit = { text, heightDp, _ ->
        Box(
            modifier = Modifier
                .height(heightDp)
                .fillMaxWidth()
        ) {
            Text(
                text = text,
                modifier = Modifier.align(Alignment.Center),
                color = Color.Black
            )
        }
    },
    dayListItem: @Composable @UiComposable LazyItemScope.(text: String, heightDp: Dp, isSelected: Boolean) -> Unit = defaultListItem,
    monthListItem: @Composable @UiComposable LazyItemScope.(text: String, heightDp: Dp, isSelected: Boolean) -> Unit = defaultListItem,
    yearListItem: @Composable @UiComposable LazyItemScope.(text: String, heightDp: Dp, isSelected: Boolean) -> Unit = defaultListItem,
    onDateChanged: (day: Int, month: Int, year: Int) -> Unit = { _, _, _ -> }
) {
    var selectedDay: Int by rememberSaveable { mutableStateOf(defaultSelectedDay) }
    var selectedMonth: Int by rememberSaveable { mutableStateOf(defaultSelectedMonth) }
    var selectedYear: Int by rememberSaveable { mutableStateOf(defaultSelectedYear) }

    val numDays: Int by remember {
        derivedStateOf {
            calculateNumDaysInMonth(selectedMonth, selectedYear)
        }
    }

    val dateElementList: ImmutableList<DateElement> =
        remember { dateElementOrder.toList().toImmutableList() }

    val onDaySelected: (newSelectedDay: Int) -> Unit = remember {
        { newSelectedDay ->
            val validatedDay = ensureValidDayNum(newSelectedDay, selectedMonth, selectedYear)
            selectedDay = validatedDay
            onDateChanged(validatedDay, selectedMonth, selectedYear)
        }
    }

    val onMonthSelected: (newSelectedMonth: Int) -> Unit = remember {
        { newSelectedMonth ->
            val validatedDay = ensureValidDayNum(selectedDay, newSelectedMonth, selectedYear)
            selectedMonth = newSelectedMonth
            selectedDay = validatedDay
            onDateChanged(validatedDay, newSelectedMonth, selectedYear)
        }
    }

    val onYearSelected: (newSelectedYear: Int) -> Unit = remember {
        { newSelectedYear ->
            val validatedDay = ensureValidDayNum(selectedDay, selectedMonth, newSelectedYear)
            selectedYear = newSelectedYear
            selectedDay = validatedDay
            onDateChanged(validatedDay, selectedMonth, newSelectedYear)
        }
    }

    DateElementRow(
        dateElementList = dateElementList,
        itemHeightDp = itemHeightDp,
        numberOfDisplayedItems = numberOfDisplayedItems,
        numDays = numDays,
        minYear = minYear,
        maxYear = maxYear,
        defaultSelectedDay = defaultSelectedDay,
        defaultSelectedMonth = defaultSelectedMonth,
        defaultSelectedYear = defaultSelectedYear,
        getDayText = getDayText,
        getMonthText = getMonthText,
        getYearText = getYearText,
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
    dateElementList: ImmutableList<DateElement>,
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
    println("COMPOSED ROW")
    Row(modifier = modifier) {
        dateElementList.forEach { dateElement ->
            when (dateElement) {
                DateElement.Day -> {
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

                DateElement.Month -> {
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

                DateElement.Year -> {
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> ScrollSelectionList(
    items: ImmutableList<T>,
    itemHeightDp: Dp,
    defaultSelectedItem: T,
    numberOfDisplayedItems: Int,
    getItemText: (T) -> String,
    selectionBackground: @Composable BoxScope.(heightDp: Dp, paddingTopDp: Dp) -> Unit,
    listItem: @Composable LazyItemScope.(text: String, heightDp: Dp, isSelected: Boolean) -> Unit,
    onItemSelected: (item: T) -> Unit,
    modifier: Modifier = Modifier
) {
    println("COMPOSED: $items")
    Box(modifier = modifier.height(itemHeightDp * numberOfDisplayedItems)) {
        selectionBackground(
            itemHeightDp,
            itemHeightDp * (numberOfDisplayedItems / 2)
        )

        val scrollState = rememberLazyListState(items.indexOf(defaultSelectedItem))

        var previousSelectedIndex: Int? by remember {
            mutableStateOf(null)
        }

        val selectedIndex by remember {
            derivedStateOf {
                scrollState.firstVisibleItemIndex
                    .coerceIn(0, items.lastIndex)
                    .also { newSelectedIndex ->
                        if (previousSelectedIndex != newSelectedIndex) {
                            previousSelectedIndex = newSelectedIndex
                            onItemSelected(items[newSelectedIndex.coerceIn(0, items.lastIndex)])
                        }
                    }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = scrollState,
            flingBehavior = rememberSnapFlingBehavior(lazyListState = scrollState)
        ) {
            items(numberOfDisplayedItems / 2) {
                Box(modifier = Modifier.height(itemHeightDp))
            }

            items(
                items = items,
                key = { item -> getItemText(item) }
            ) { item ->
                val isSelected by remember {
                    derivedStateOf { items[selectedIndex.coerceIn(0, items.lastIndex)] == item }
                }
                listItem(getItemText(item), itemHeightDp, isSelected)
            }

            items(numberOfDisplayedItems / 2) {
                Box(modifier = Modifier.height(itemHeightDp))
            }
        }
    }
}

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


@Stable
sealed class DateElement {
    object Year : DateElement()
    object Month : DateElement()
    object Day : DateElement()
}
