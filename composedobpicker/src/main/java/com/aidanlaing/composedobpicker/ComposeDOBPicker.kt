package com.aidanlaing.composedobpicker

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.lerp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar
import kotlin.math.absoluteValue

// TODO code cleanup and optimization
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
    itemHeightDp: Dp = 56.dp,
    numberOfDisplayedItems: Int = 5,
    defaultSelectedDay: Int = 1,
    defaultSelectedMonth: Month = Month.JAN,
    defaultSelectedYear: Int = 2000,
    minYear: Int = 1900,
    maxYear: Int = Calendar.getInstance().get(Calendar.YEAR),
    animateTextStyles: Boolean = true,
    fontSizeDecreasePerStep: TextUnit = 2.sp,
    minTextSize: TextUnit = 14.sp,
    alphaDecreasePerStep: Float = 0.2f,
    minAlpha: Float = 0.4f,
    dateElementOrder: Triple<DateElement, DateElement, DateElement> = Triple(
        DateElement.YEAR,
        DateElement.MONTH,
        DateElement.DAY
    ),
    colors: ComposeDOBPickerColors = ComposeDOBPickerColors(),
    textStyles: ComposeDOBPickerTextStyles = ComposeDOBPickerTextStyles(),
    textStrings: ComposeDOBPickerTextStrings = ComposeDOBPickerTextStrings(),
    selectionBackground: @Composable BoxScope.(
        color: Color,
        height: Dp,
        paddingTop: Dp
    ) -> Unit = { color, height, paddingTop ->
        Box(
            modifier = Modifier
                .padding(top = paddingTop)
                .height(height)
                .fillMaxWidth()
                .background(color)
        )
    },
    defaultListItem: @Composable LazyItemScope.(
        text: String,
        style: TextStyle,
        height: Dp
    ) -> Unit = { text, style, height ->
        Text(
            text = text,
            style = style,
            modifier = Modifier
                .height(height)
                .fillMaxWidth()
                .wrapContentHeight(),
            textAlign = TextAlign.Center
        )
    },
    dayListItem: @Composable LazyItemScope.(text: String, style: TextStyle, height: Dp) -> Unit = defaultListItem,
    monthListItem: @Composable LazyItemScope.(text: String, style: TextStyle, height: Dp) -> Unit = defaultListItem,
    yearListItem: @Composable LazyItemScope.(text: String, style: TextStyle, height: Dp) -> Unit = defaultListItem
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
                    itemHeightDp = itemHeightDp,
                    numberOfDisplayedItems = numberOfDisplayedItems,
                    defaultSelectedDay = defaultSelectedDay,
                    animateTextStyles = animateTextStyles,
                    fontSizeDecreasePerStep = fontSizeDecreasePerStep,
                    minTextSize = minTextSize,
                    alphaDecreasePerStep = alphaDecreasePerStep,
                    minAlpha = minAlpha,
                    selectionBackground = selectionBackground,
                    listItem = dayListItem,
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
                    itemHeightDp = itemHeightDp,
                    numberOfDisplayedItems = numberOfDisplayedItems,
                    defaultSelectedMonth = defaultSelectedMonth,
                    animateTextStyles = animateTextStyles,
                    fontSizeDecreasePerStep = fontSizeDecreasePerStep,
                    minTextSize = minTextSize,
                    alphaDecreasePerStep = alphaDecreasePerStep,
                    minAlpha = minAlpha,
                    selectionBackground = selectionBackground,
                    listItem = monthListItem,
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
                    itemHeightDp = itemHeightDp,
                    numberOfDisplayedItems = numberOfDisplayedItems,
                    defaultSelectedYear = defaultSelectedYear,
                    minYear = minYear,
                    maxYear = maxYear,
                    animateTextStyles = animateTextStyles,
                    fontSizeDecreasePerStep = fontSizeDecreasePerStep,
                    minTextSize = minTextSize,
                    alphaDecreasePerStep = alphaDecreasePerStep,
                    minAlpha = minAlpha,
                    selectionBackground = selectionBackground,
                    listItem = yearListItem,
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
    itemHeightDp: Dp,
    numberOfDisplayedItems: Int,
    defaultSelectedDay: Int,
    animateTextStyles: Boolean,
    fontSizeDecreasePerStep: TextUnit,
    minTextSize: TextUnit,
    alphaDecreasePerStep: Float,
    minAlpha: Float,
    selectionBackground: @Composable BoxScope.(color: Color, height: Dp, paddingTop: Dp) -> Unit,
    listItem: @Composable LazyItemScope.(text: String, style: TextStyle, height: Dp) -> Unit,
    onDaySelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val numDays: Int = calculateNumDaysInMonth(selectedMonth, selectedYear)
    ScrollSelectionList(
        itemHeightDp,
        items = (1..numDays).toList(),
        selectedItem = selectedDay ?: defaultSelectedDay,
        getItemText = { item -> item.toString() },
        textStyle = textStyles.yearItemTextStyle,
        textColor = colors.dayUnselectedTextColor,
        selectedTextColor = colors.daySelectedTextColor,
        selectionBackgroundColor = colors.selectionBackgroundColor,
        numberOfDisplayedItems = numberOfDisplayedItems,
        animateTextStyles = animateTextStyles,
        fontSizeDecreasePerStep = fontSizeDecreasePerStep,
        minTextSize = minTextSize,
        alphaDecreasePerStep = alphaDecreasePerStep,
        minAlpha = minAlpha,
        selectionBackground = selectionBackground,
        listItem = listItem,
        onItemSelected = { year -> onDaySelected(year) },
        modifier = modifier
    )
}

@Composable
private fun MonthPicker(
    selectedMonth: Month?,
    colors: ComposeDOBPickerColors,
    textStyles: ComposeDOBPickerTextStyles,
    monthNames: Map<Month, String>,
    itemHeightDp: Dp,
    defaultSelectedMonth: Month,
    numberOfDisplayedItems: Int,
    animateTextStyles: Boolean,
    fontSizeDecreasePerStep: TextUnit,
    minTextSize: TextUnit,
    alphaDecreasePerStep: Float,
    minAlpha: Float,
    selectionBackground: @Composable BoxScope.(color: Color, height: Dp, paddingTop: Dp) -> Unit,
    listItem: @Composable LazyItemScope.(text: String, style: TextStyle, height: Dp) -> Unit,
    onMonthSelected: (Month) -> Unit,
    modifier: Modifier = Modifier
) {
    ScrollSelectionList(
        itemHeightDp = itemHeightDp,
        items = Month.values().toList(),
        selectedItem = selectedMonth ?: defaultSelectedMonth,
        textStyle = textStyles.yearItemTextStyle,
        textColor = colors.monthUnselectedTextColor,
        getItemText = { item -> monthNames[item] ?: "" },
        selectedTextColor = colors.monthSelectedTextColor,
        selectionBackgroundColor = colors.selectionBackgroundColor,
        numberOfDisplayedItems = numberOfDisplayedItems,
        animateTextStyles = animateTextStyles,
        fontSizeDecreasePerStep = fontSizeDecreasePerStep,
        minTextSize = minTextSize,
        alphaDecreasePerStep = alphaDecreasePerStep,
        minAlpha = minAlpha,
        selectionBackground = selectionBackground,
        listItem = listItem,
        onItemSelected = { month -> onMonthSelected(month) },
        modifier = modifier
    )
}

@Composable
private fun YearPicker(
    selectedYear: Int?,
    colors: ComposeDOBPickerColors,
    textStyles: ComposeDOBPickerTextStyles,
    itemHeightDp: Dp,
    numberOfDisplayedItems: Int,
    defaultSelectedYear: Int,
    minYear: Int,
    maxYear: Int,
    animateTextStyles: Boolean,
    fontSizeDecreasePerStep: TextUnit,
    minTextSize: TextUnit,
    alphaDecreasePerStep: Float,
    minAlpha: Float,
    selectionBackground: @Composable BoxScope.(color: Color, height: Dp, paddingTop: Dp) -> Unit,
    listItem: @Composable LazyItemScope.(text: String, style: TextStyle, height: Dp) -> Unit,
    onYearSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    ScrollSelectionList(
        itemHeightDp = itemHeightDp,
        items = (minYear..maxYear).toList(),
        selectedItem = selectedYear ?: defaultSelectedYear.coerceIn(minYear, maxYear),
        getItemText = { item -> item.toString() },
        textStyle = textStyles.yearItemTextStyle,
        textColor = colors.yearUnselectedTextColor,
        selectedTextColor = colors.yearSelectedTextColor,
        selectionBackgroundColor = colors.selectionBackgroundColor,
        numberOfDisplayedItems = numberOfDisplayedItems,
        animateTextStyles = animateTextStyles,
        fontSizeDecreasePerStep = fontSizeDecreasePerStep,
        minTextSize = minTextSize,
        alphaDecreasePerStep = alphaDecreasePerStep,
        minAlpha = minAlpha,
        selectionBackground = selectionBackground,
        listItem = listItem,
        onItemSelected = { year -> onYearSelected(year) },
        modifier = modifier
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> ScrollSelectionList(
    itemHeightDp: Dp,
    items: List<T>,
    selectedItem: T,
    getItemText: (T) -> String,
    textStyle: TextStyle,
    textColor: Color,
    selectedTextColor: Color,
    selectionBackgroundColor: Color,
    numberOfDisplayedItems: Int,
    animateTextStyles: Boolean,
    fontSizeDecreasePerStep: TextUnit,
    minTextSize: TextUnit,
    alphaDecreasePerStep: Float,
    minAlpha: Float,
    selectionBackground: @Composable BoxScope.(color: Color, height: Dp, paddingTop: Dp) -> Unit,
    listItem: @Composable LazyItemScope.(text: String, style: TextStyle, height: Dp) -> Unit,
    onItemSelected: (item: T) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.height(itemHeightDp * numberOfDisplayedItems)) {
        selectionBackground(
            selectionBackgroundColor,
            itemHeightDp,
            itemHeightDp * (numberOfDisplayedItems / 2)
        )

        val scrollState = rememberLazyListState(items.indexOf(selectedItem))
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = scrollState,
            flingBehavior = rememberSnapFlingBehavior(lazyListState = scrollState)
        ) {
            items(numberOfDisplayedItems / 2) {
                Box(modifier = Modifier.height(itemHeightDp))
            }
            items(items = items) { item ->
                val index = items.indexOf(item)

                val selectedIndex by remember {
                    derivedStateOf {
                        scrollState.firstVisibleItemIndex
                            .coerceIn(0, items.lastIndex)
                            .also { index -> onItemSelected(items[index]) }
                    }
                }

                val targetTextStyle = getItemTextStyle(
                    textStyle,
                    selectedIndex,
                    index,
                    fontSizeDecreasePerStep,
                    minTextSize,
                    alphaDecreasePerStep,
                    minAlpha,
                    selectedTextColor,
                    textColor
                )

                val displayTextStyle = if (animateTextStyles) {
                    val animatedTextStyle by animateTextStyleAsState(
                        targetValue = targetTextStyle,
                        animationSpec = spring()
                    )
                    animatedTextStyle
                } else {
                    targetTextStyle
                }

                listItem(getItemText(item), displayTextStyle, itemHeightDp)

            }
            items(numberOfDisplayedItems / 2) {
                Box(modifier = Modifier.height(itemHeightDp))
            }
        }
    }
}

@Composable
@ReadOnlyComposable
private fun getItemTextStyle(
    baseTextStyle: TextStyle,
    selectedIndex: Int,
    index: Int,
    fontSizeDecreasePerStep: TextUnit,
    minTextSize: TextUnit,
    alphaDecreasePerStep: Float,
    minAlpha: Float,
    selectedTextColor: Color,
    textColor: Color
): TextStyle {
    val indexCountFromSelected = (selectedIndex - index).absoluteValue

    val targetFontSize = LocalDensity.current.run {
        (baseTextStyle.fontSize.toPx() - indexCountFromSelected * fontSizeDecreasePerStep.toPx())
            .coerceAtLeast(minTextSize.toPx()).toSp()
    }

    val targetAlpha = (1f - indexCountFromSelected * alphaDecreasePerStep)
        .coerceAtLeast(minAlpha)

    return baseTextStyle.copy(
        fontSize = targetFontSize,
        color = if (selectedIndex == index) {
            selectedTextColor
        } else {
            textColor
        }.copy(alpha = targetAlpha)
    )
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
    val selectionBackgroundColor: Color = Color(0x809496A1),
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
