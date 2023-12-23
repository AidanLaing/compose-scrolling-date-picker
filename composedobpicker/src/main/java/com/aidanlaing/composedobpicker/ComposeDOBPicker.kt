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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
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
fun ComposeDOBPicker(
    defaultListItem: @Composable LazyItemScope.(
        text: String,
        heightDp: Dp,
        isSelected: Boolean
    ) -> Unit,
    onDateOfBirthChanged: (dateOfBirth: DateOfBirth) -> Unit,
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
            onDateOfBirthChanged(DateOfBirth(validatedDay, selectedMonth, selectedYear))
        }
    }

    val onMonthSelected: (newSelectedMonth: Int) -> Unit = remember {
        { newSelectedMonth ->
            val validatedDay = ensureValidDayNum(selectedDay, newSelectedMonth, selectedYear)
            selectedDay = validatedDay
            selectedMonth = newSelectedMonth
            onDateOfBirthChanged(DateOfBirth(validatedDay, newSelectedMonth, selectedYear))
        }
    }

    val onYearSelected: (newSelectedYear: Int) -> Unit = remember {
        { newSelectedYear ->
            val validatedDay = ensureValidDayNum(selectedDay, selectedMonth, newSelectedYear)
            selectedDay = validatedDay
            selectedYear = newSelectedYear
            onDateOfBirthChanged(DateOfBirth(validatedDay, selectedMonth, newSelectedYear))
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

enum class DateOfBirthElement {
    Day, Month, Year
}

enum class Month { January, February, March, April, May, June, July, August, September, October, November, December }

data class DateOfBirth(val day: Int, val month: Int, val year: Int) {

    fun monthAsEnumType(): Month = Month.values()[month]

    /**
     * Supported patterns
     * Day:     dd      -   numeric day padded length
     *          d       -   numeric day not padded
     *          z       -   day postfix (st, nd, rd, th)
     * Month:   mmmm    -   text month full length
     *          mmm     -   text month shortened length
     *          mm      -   numeric month padded length
     *          m       -   numeric month not padded
     * Year:    yyyy    -   numeric year full length
     */
    fun asText(
        pattern: String = "mmmm dz, yyyy",
        monthNames: List<String> = Month.values().map { month -> month.name }.toList(),
        padChar: Char = '0',
        padLength: Int = 2,
        shortenedMonthLength: Int = 3,
        locale: Locale = Locale.current
    ): String = pattern
        .replace("yyyy", year.toString())
        .replace("dd", day.toString().padStart(length = padLength, padChar = padChar))
        .replace("d", day.toString())
        .replace("z", getDayPostFix())
        .replace("mmmm", monthNames[month].uppercase())
        .replace("mmm", monthNames[month].take(shortenedMonthLength).uppercase())
        .replace("mm", (month + 1).toString().padStart(length = padLength, padChar = padChar))
        .replace("m", (month + 1).toString())
        .lowercase()
        .capitalize(locale = locale)

    private fun getDayPostFix(): String {
        val dayString = day.toString()
        return when {
            dayString.endsWith("1") && day != 11 -> "st"
            dayString.endsWith("2") && day != 12 -> "nd"
            dayString.endsWith("3") && day != 13 -> "rd"
            else -> "th"
        }
    }
}