package com.aidanlaing.composedobpicker

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.lerp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.absoluteValue

// TODO remove magic numbers
// TODO code cleanup and optimization
// TODO max and min date params
// TODO landscape mode / tablet
// TODO code cleanup and optimization
// TODO dialog / bottom sheet options?
// TODO Customization (Material theming support)? MORE COMPOSABLE
// TODO reduce gradle dependencies, min sdk
// TODO integration testing with CI
// TODO README
// TODO Maven publishing
// TODO Android dev post
@Composable
fun ComposeDOBPicker(
    modifier: Modifier = Modifier,
    dateElementOrder: Triple<DateElement, DateElement, DateElement> = Triple(
        DateElement.YEAR,
        DateElement.MONTH,
        DateElement.DAY
    ),
    colors: ComposeDOBPickerColors = ComposeDOBPickerColors(),
    textStyles: ComposeDOBPickerTextStyles = ComposeDOBPickerTextStyles(),
    textStrings: ComposeDOBPickerTextStrings = ComposeDOBPickerTextStrings()
) {
    var selectedDay: Int? by rememberSaveable { mutableStateOf(null) }
    var selectedMonth: Month? by rememberSaveable { mutableStateOf(null) }
    var selectedYear: Int? by rememberSaveable { mutableStateOf(null) }
    Row(modifier = modifier) {
        dateElementOrder.toList().forEach { dateElement ->
            when (dateElement) {
                DateElement.DAY -> DayPicker(
                    selectedDay = selectedDay,
                    selectedMonth = selectedMonth,
                    selectedYear = selectedYear,
                    colors = colors,
                    textStyles = textStyles,
                    onDaySelected = { day ->
                        selectedDay = ensureValidDayNum(day, selectedMonth, selectedYear)
                    },
                    modifier = Modifier.weight(1f)
                )

                DateElement.MONTH -> MonthPicker(
                    selectedMonth = selectedMonth,
                    colors = colors,
                    monthNames = textStrings.monthNames,
                    textStyles = textStyles,
                    onMonthSelected = { month ->
                        selectedMonth = month
                        selectedDay = ensureValidDayNum(selectedDay, month, selectedYear)
                    },
                    modifier = Modifier.weight(1f)
                )

                DateElement.YEAR -> YearPicker(
                    selectedYear = selectedYear,
                    colors = colors,
                    textStyles = textStyles,
                    onYearSelected = { year ->
                        selectedYear = year
                        selectedDay = ensureValidDayNum(selectedDay, selectedMonth, year)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

    }
}

@Composable
private fun DayPicker(
    selectedDay: Int?,
    selectedMonth: Month?,
    selectedYear: Int?,
    colors: ComposeDOBPickerColors,
    textStyles: ComposeDOBPickerTextStyles,
    onDaySelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val numDays: Int = calculateNumDaysInMonth(selectedMonth, selectedYear)
    ScrollSelectionList(
        itemHeight = 56.dp,
        items = (1..numDays).toList(),
        selectedItem = selectedDay ?: 1,
        getItemText = { item -> item.toString() },
        textStyle = textStyles.yearItemTextStyle,
        textColor = colors.dayUnselectedTextColor,
        selectedTextColor = colors.daySelectedTextColor,
        selectionBoxColor = colors.selectionBoxColor,
        onItemSelected = { year -> onDaySelected(year) },
        modifier = modifier,
        numberOfDisplayedItems = 5
    )
}

@Composable
private fun MonthPicker(
    selectedMonth: Month?,
    colors: ComposeDOBPickerColors,
    textStyles: ComposeDOBPickerTextStyles,
    monthNames: Map<Month, String>,
    onMonthSelected: (Month) -> Unit,
    modifier: Modifier = Modifier
) {
    ScrollSelectionList(
        itemHeight = 56.dp,
        items = Month.values().toList(),
        selectedItem = selectedMonth ?: Month.JAN,
        textStyle = textStyles.yearItemTextStyle,
        textColor = colors.monthUnselectedTextColor,
        getItemText = { item -> monthNames[item] ?: "" },
        selectedTextColor = colors.monthSelectedTextColor,
        selectionBoxColor = colors.selectionBoxColor,
        onItemSelected = { month -> onMonthSelected(month) },
        modifier = modifier,
        numberOfDisplayedItems = 5
    )
}

@Composable
private fun YearPicker(
    selectedYear: Int?,
    onYearSelected: (Int) -> Unit,
    colors: ComposeDOBPickerColors,
    textStyles: ComposeDOBPickerTextStyles,
    modifier: Modifier = Modifier
) {
    ScrollSelectionList(
        itemHeight = 56.dp,
        items = (1900..2023).toList(),
        selectedItem = selectedYear ?: 2000,
        getItemText = { item -> item.toString() },
        textStyle = textStyles.yearItemTextStyle,
        textColor = colors.yearUnselectedTextColor,
        selectedTextColor = colors.yearSelectedTextColor,
        selectionBoxColor = colors.selectionBoxColor,
        onItemSelected = { year -> onYearSelected(year) },
        modifier = modifier,
        numberOfDisplayedItems = 5
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> ScrollSelectionList(
    itemHeight: Dp,
    items: List<T>,
    selectedItem: T,
    getItemText: (T) -> String,
    textStyle: TextStyle,
    textColor: Color,
    selectedTextColor: Color,
    selectionBoxColor: Color,
    numberOfDisplayedItems: Int,
    onItemSelected: (item: T) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.height(itemHeight * numberOfDisplayedItems)) {
        Box(
            modifier = Modifier
                .padding(top = itemHeight * 2)
                .background(color = selectionBoxColor)
                .height(itemHeight)
                .fillMaxWidth()
        )

        val scrollState = rememberLazyListState(items.indexOf(selectedItem))
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = scrollState,
            flingBehavior = rememberSnapFlingBehavior(lazyListState = scrollState)
        ) {
            items(numberOfDisplayedItems / 2) {
                Box(modifier = Modifier.height(itemHeight))
            }
            items(items = items) { item ->
                val index = items.indexOf(item)
                Box(
                    modifier = Modifier
                        .height(itemHeight)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    val selectedIndex by remember {
                        derivedStateOf {
                            scrollState.firstVisibleItemIndex.coerceIn(0, items.lastIndex)
                                .also { index -> onItemSelected(items[index]) }
                        }
                    }
                    val indexCountFromSelected = (selectedIndex - index).absoluteValue
                    val targetFontSize = LocalDensity.current.run {
                        (textStyle.fontSize.toPx() - indexCountFromSelected * 2.sp.toPx())
                            .coerceAtLeast(14.sp.toPx()).toSp()
                    }
                    val targetAlpha = (1f - indexCountFromSelected * 0.3f)
                        .coerceAtLeast(0.4f)
                    val animatedTextStyle by animateTextStyleAsState(
                        targetValue = textStyle.copy(
                            fontSize = targetFontSize,
                            color = if (selectedIndex == index) {
                                selectedTextColor
                            } else {
                                textColor
                            }.copy(alpha = targetAlpha)
                        ),
                        animationSpec = spring()
                    )
                    Text(
                        text = getItemText(item),
                        style = animatedTextStyle
                    )
                }
            }
            items(numberOfDisplayedItems / 2) {
                Box(modifier = Modifier.height(itemHeight))
            }
        }
    }
}

@Composable
private fun animateTextStyleAsState(
    targetValue: TextStyle,
    animationSpec: AnimationSpec<Float> = spring(),
    finishedListener: ((TextStyle) -> Unit)? = null
): State<TextStyle> {
    val animation = remember { Animatable(0f) }
    var previousTextStyle by remember { mutableStateOf(targetValue) }
    var nextTextStyle by remember { mutableStateOf(targetValue) }

    val textStyleState = remember(animation.value) {
        derivedStateOf {
            lerp(previousTextStyle, nextTextStyle, animation.value)
        }
    }

    LaunchedEffect(targetValue, animationSpec) {
        previousTextStyle = textStyleState.value
        nextTextStyle = targetValue
        animation.snapTo(0f)
        animation.animateTo(1f, animationSpec)
        finishedListener?.invoke(textStyleState.value)
    }

    return textStyleState
}

private fun calculateNumDaysInMonth(selectedMonth: Month?, selectedYear: Int?): Int =
    when (selectedMonth) {
        null, Month.JAN, Month.MAR, Month.MAY, Month.JUL, Month.AUG, Month.OCT, Month.DEC -> 31
        Month.APR, Month.JUN, Month.SEP, Month.NOV -> 30
        Month.FEB -> if (calculateIsLeapYear(selectedYear)) 29 else 28
    }

private fun calculateIsLeapYear(selectedYear: Int?): Boolean =
    when {
        selectedYear == null -> true
        selectedYear % 4 == 0 -> if (selectedYear % 100 == 0) selectedYear % 400 == 0 else true
        else -> false
    }

private fun ensureValidDayNum(selectedDay: Int?, selectedMonth: Month?, selectedYear: Int?): Int? {
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

@Immutable
data class ComposeDOBPickerColors(
    val selectionBoxColor: Color = Color(0x809496A1),
    val yearUnselectedTextColor: Color = Color(0xFFB3B5BD),
    val yearSelectedTextColor: Color = Color(0xFF404252),
    val monthUnselectedTextColor: Color = Color(0xFFB3B5BD),
    val monthSelectedTextColor: Color = Color(0xFF404252),
    val dayUnselectedTextColor: Color = Color(0xFFB3B5BD),
    val daySelectedTextColor: Color = Color(0xFF404252)
)

@Immutable
data class ComposeDOBPickerTextStrings(
    val monthNames: Map<Month, String> = mapOf(
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
    )
)

@Immutable
data class ComposeDOBPickerTextStyles(
    val yearItemTextStyle: TextStyle = TextStyle(
        fontSize = TextUnit(18f, TextUnitType.Sp),
        fontWeight = FontWeight.Medium,
        fontStyle = FontStyle.Normal
    ),
    val monthItemTextStyle: TextStyle = TextStyle(
        fontSize = TextUnit(18f, TextUnitType.Sp),
        fontWeight = FontWeight.Medium,
        fontStyle = FontStyle.Normal
    ),
    val dayItemTextStyle: TextStyle = TextStyle(
        fontSize = TextUnit(18f, TextUnitType.Sp),
        fontWeight = FontWeight.Medium,
        fontStyle = FontStyle.Normal
    )
)
