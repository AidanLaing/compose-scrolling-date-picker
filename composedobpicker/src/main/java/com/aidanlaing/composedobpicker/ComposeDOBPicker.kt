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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
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
    defaultSelectedMonth: Month = Month.JAN,
    defaultSelectedYear: Int = 2000,
    minYear: Int = 1900,
    maxYear: Int = Calendar.getInstance().get(Calendar.YEAR),
    dateElementOrder: Triple<DateElement, DateElement, DateElement> = Triple(
        DateElement.YEAR,
        DateElement.MONTH,
        DateElement.DAY
    ),
    monthNames: Map<Month, String> = mapOf(
        Month.JAN to "January",
        Month.FEB to "February",
        Month.MAR to "March",
        Month.APR to "April",
        Month.MAY to "May",
        Month.JUN to "June",
        Month.JUL to "July",
        Month.AUG to "August",
        Month.SEP to "September",
        Month.OCT to "October",
        Month.NOV to "November",
        Month.DEC to "December"
    ),
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
    defaultListItem: @Composable LazyItemScope.(
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
    dayListItem: @Composable LazyItemScope.(text: String, heightDp: Dp, isSelected: Boolean) -> Unit = defaultListItem,
    monthListItem: @Composable LazyItemScope.(text: String, heightDp: Dp, isSelected: Boolean) -> Unit = defaultListItem,
    yearListItem: @Composable LazyItemScope.(text: String, heightDp: Dp, isSelected: Boolean) -> Unit = defaultListItem,
    onDateChanged: (day: Int, month: Month, year: Int) -> Unit = { _, _, _ -> }
) {
    var selectedDay: Int? by rememberSaveable { mutableStateOf(null) }
    var selectedMonth: Month? by rememberSaveable { mutableStateOf(null) }
    var selectedYear: Int? by rememberSaveable { mutableStateOf(null) }

    val dayItems: List<Int> by remember {
        derivedStateOf {
            val numDays = calculateNumDaysInMonth(selectedMonth, selectedYear)
            (1..numDays).toList()
        }
    }

    val monthItems: List<Month> = remember { Month.values().toList() }
    val yearItems: List<Int> = remember { (minYear..maxYear).toList() }

    DateElementRow(
        selectedDay = selectedDay,
        selectedMonth = selectedMonth,
        selectedYear = selectedYear,
        dayItems = dayItems,
        monthItems = monthItems,
        yearItems = yearItems,
        itemHeightDp = itemHeightDp,
        numberOfDisplayedItems = numberOfDisplayedItems,
        defaultSelectedDay = defaultSelectedDay,
        defaultSelectedMonth = defaultSelectedMonth,
        defaultSelectedYear = defaultSelectedYear,
        minYear = minYear,
        maxYear = maxYear,
        dateElementOrder = dateElementOrder,
        monthNames = monthNames,
        selectionBackground = selectionBackground,
        dayListItem = dayListItem,
        monthListItem = monthListItem,
        yearListItem = yearListItem,
        onDateChanged = { day, month, year ->
            selectedDay = day
            selectedMonth = month
            selectedYear = year
            onDateChanged(day, month, year)
        },
        modifier = modifier
    )
}

@Composable
fun DateElementRow(
    selectedDay: Int?,
    selectedMonth: Month?,
    selectedYear: Int?,
    dayItems: List<Int>,
    monthItems: List<Month>,
    yearItems: List<Int>,
    itemHeightDp: Dp,
    numberOfDisplayedItems: Int,
    defaultSelectedDay: Int,
    defaultSelectedMonth: Month,
    defaultSelectedYear: Int,
    minYear: Int,
    maxYear: Int,
    dateElementOrder: Triple<DateElement, DateElement, DateElement>,
    monthNames: Map<Month, String>,
    selectionBackground: @Composable BoxScope.(heightDp: Dp, paddingTopDp: Dp) -> Unit,
    dayListItem: @Composable LazyItemScope.(text: String, heightDp: Dp, isSelected: Boolean) -> Unit,
    monthListItem: @Composable LazyItemScope.(text: String, heightDp: Dp, isSelected: Boolean) -> Unit,
    yearListItem: @Composable LazyItemScope.(text: String, heightDp: Dp, isSelected: Boolean) -> Unit,
    onDateChanged: (day: Int, month: Month, year: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        dateElementOrder.toList().forEach { dateElement ->
            when (dateElement) {
                DateElement.DAY -> DayPicker(
                    dayItems = dayItems,
                    itemHeightDp = itemHeightDp,
                    numberOfDisplayedItems = numberOfDisplayedItems,
                    defaultSelectedDay = defaultSelectedDay,
                    selectionBackground = selectionBackground,
                    listItem = dayListItem,
                    onDaySelected = { newSelectedDay ->
                        val validatedDay =
                            ensureValidDayNum(newSelectedDay, selectedMonth, selectedYear)
                        onDateChanged(
                            validatedDay ?: defaultSelectedDay,
                            selectedMonth ?: defaultSelectedMonth,
                            selectedYear ?: defaultSelectedYear
                        )
                    },
                    modifier = Modifier.weight(1f)
                )

                DateElement.MONTH -> MonthPicker(
                    monthItems = monthItems,
                    monthNames = monthNames,
                    itemHeightDp = itemHeightDp,
                    numberOfDisplayedItems = numberOfDisplayedItems,
                    defaultSelectedMonth = defaultSelectedMonth,
                    selectionBackground = selectionBackground,
                    listItem = monthListItem,
                    onMonthSelected = { newSelectedMonth ->
                        val validatedDay =
                            ensureValidDayNum(selectedDay, newSelectedMonth, selectedYear)
                        onDateChanged(
                            validatedDay ?: defaultSelectedDay,
                            newSelectedMonth,
                            selectedYear ?: defaultSelectedYear
                        )
                    },
                    modifier = Modifier.weight(1f)
                )

                DateElement.YEAR -> YearPicker(
                    yearItems = yearItems,
                    itemHeightDp = itemHeightDp,
                    numberOfDisplayedItems = numberOfDisplayedItems,
                    defaultSelectedYear = defaultSelectedYear.coerceIn(minYear, maxYear),
                    selectionBackground = selectionBackground,
                    listItem = yearListItem,
                    onYearSelected = { newSelectedYear ->
                        val validatedDay =
                            ensureValidDayNum(selectedDay, selectedMonth, newSelectedYear)
                        onDateChanged(
                            validatedDay ?: defaultSelectedDay,
                            selectedMonth ?: defaultSelectedMonth,
                            newSelectedYear
                        )
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun DayPicker(
    dayItems: List<Int>,
    itemHeightDp: Dp,
    numberOfDisplayedItems: Int,
    defaultSelectedDay: Int,
    selectionBackground: @Composable BoxScope.(heightDp: Dp, paddingTopDp: Dp) -> Unit,
    listItem: @Composable LazyItemScope.(text: String, heightDp: Dp, isSelected: Boolean) -> Unit,
    onDaySelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    ScrollSelectionList(
        items = dayItems,
        itemHeightDp = itemHeightDp,
        defaultSelectedItem = defaultSelectedDay,
        getItemText = { item -> item.toString() },
        numberOfDisplayedItems = numberOfDisplayedItems,
        selectionBackground = selectionBackground,
        listItem = listItem,
        onItemSelected = { year -> onDaySelected(year) },
        modifier = modifier
    )
}

@Composable
fun MonthPicker(
    monthItems: List<Month>,
    monthNames: Map<Month, String>,
    itemHeightDp: Dp,
    defaultSelectedMonth: Month,
    numberOfDisplayedItems: Int,
    selectionBackground: @Composable BoxScope.(heightDp: Dp, paddingTopDp: Dp) -> Unit,
    listItem: @Composable LazyItemScope.(text: String, heightDp: Dp, isSelected: Boolean) -> Unit,
    onMonthSelected: (Month) -> Unit,
    modifier: Modifier = Modifier
) {
    ScrollSelectionList(
        items = monthItems,
        itemHeightDp = itemHeightDp,
        defaultSelectedItem = defaultSelectedMonth,
        getItemText = { item -> monthNames[item] ?: "" },
        numberOfDisplayedItems = numberOfDisplayedItems,
        selectionBackground = selectionBackground,
        listItem = listItem,
        onItemSelected = { month -> onMonthSelected(month) },
        modifier = modifier
    )
}

@Composable
fun YearPicker(
    yearItems: List<Int>,
    itemHeightDp: Dp,
    defaultSelectedYear: Int,
    numberOfDisplayedItems: Int,
    selectionBackground: @Composable BoxScope.(heightDp: Dp, paddingTopDp: Dp) -> Unit,
    listItem: @Composable LazyItemScope.(text: String, heightDp: Dp, isSelected: Boolean) -> Unit,
    onYearSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    ScrollSelectionList(
        items = yearItems,
        itemHeightDp = itemHeightDp,
        defaultSelectedItem = defaultSelectedYear,
        getItemText = { item -> item.toString() },
        numberOfDisplayedItems = numberOfDisplayedItems,
        selectionBackground = selectionBackground,
        listItem = listItem,
        onItemSelected = { year -> onYearSelected(year) },
        modifier = modifier
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> ScrollSelectionList(
    items: List<T>,
    itemHeightDp: Dp,
    defaultSelectedItem: T,
    getItemText: (T) -> String,
    numberOfDisplayedItems: Int,
    selectionBackground: @Composable BoxScope.(heightDp: Dp, paddingTopDp: Dp) -> Unit,
    listItem: @Composable LazyItemScope.(text: String, heightDp: Dp, isSelected: Boolean) -> Unit,
    onItemSelected: (item: T) -> Unit,
    modifier: Modifier = Modifier
) {
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
            }
        }

        if (previousSelectedIndex != selectedIndex) {
            previousSelectedIndex = selectedIndex
            onItemSelected(items[selectedIndex.coerceIn(0, items.lastIndex)])
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

internal fun calculateNumDaysInMonth(selectedMonth: Month?, selectedYear: Int?): Int =
    when (selectedMonth) {
        null, Month.JAN, Month.MAR, Month.MAY, Month.JUL, Month.AUG, Month.OCT, Month.DEC -> 31
        Month.APR, Month.JUN, Month.SEP, Month.NOV -> 30
        Month.FEB -> if (calculateIsLeapYear(selectedYear)) 29 else 28
    }

internal fun calculateIsLeapYear(selectedYear: Int?): Boolean =
    when {
        selectedYear == null -> true
        selectedYear % 4 == 0 -> if (selectedYear % 100 == 0) selectedYear % 400 == 0 else true
        else -> false
    }

internal fun ensureValidDayNum(selectedDay: Int?, selectedMonth: Month?, selectedYear: Int?): Int? {
    if (selectedDay == null) return null

    val numDaysInMonth = calculateNumDaysInMonth(selectedMonth, selectedYear)
    if (selectedDay > numDaysInMonth) return numDaysInMonth

    return selectedDay
}

enum class DateElement {
    YEAR, MONTH, DAY
}

enum class Month {
    JAN, FEB, MAR, APR, MAY, JUN, JUL, AUG, SEP, OCT, NOV, DEC
}
