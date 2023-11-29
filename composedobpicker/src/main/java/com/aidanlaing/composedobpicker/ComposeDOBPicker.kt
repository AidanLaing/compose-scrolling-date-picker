package com.aidanlaing.composedobpicker

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
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
import kotlin.math.absoluteValue

// TODO fast scroll bug
// TODO code cleanup and optimization
// TODO max and min date params
// TODO animations
// TODO landscape mode / tablet
// TODO code cleanup and optimization
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
    itemSpacingDp: Dp = 4.dp,
    dateElementOrder: Triple<DateElement, DateElement, DateElement> = Triple(
        DateElement.YEAR,
        DateElement.MONTH,
        DateElement.DAY
    ),
    colors: ComposeDOBPickerColors = ComposeDOBPickerColors(),
    textStyles: ComposeDOBPickerTextStyles = ComposeDOBPickerTextStyles(),
    textStrings: ComposeDOBPickerTextStrings = ComposeDOBPickerTextStrings()
) {
    var selectedDateElement: DateElement by rememberSaveable { mutableStateOf(DateElement.YEAR) }
    var selectedDay: Int? by rememberSaveable { mutableStateOf(null) }
    var selectedMonth: Month? by rememberSaveable { mutableStateOf(null) }
    var selectedYear: Int? by rememberSaveable { mutableStateOf(null) }
    Column(modifier = modifier) {
        DateElements(
            dateElementOrder = dateElementOrder,
            selectedDateElement = selectedDateElement,
            selectedDay = selectedDay,
            selectedMonth = selectedMonth,
            selectedYear = selectedYear,
            textStrings = textStrings,
            colors = colors,
            textStyles = textStyles,
            itemSpacingDp = itemSpacingDp,
            onDateElementClick = { dateElement -> selectedDateElement = dateElement }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            YearPicker(
                selectedYear = selectedYear,
                colors = colors,
                textStyles = textStyles,
                onYearSelected = { year ->
                    selectedYear = year
                    selectedDay = ensureValidDayNum(selectedDay, selectedMonth, selectedYear)
                },
                modifier = Modifier.weight(1f)
            )
            MonthPicker(
                selectedMonth = selectedMonth,
                itemSpacingDp = itemSpacingDp,
                colors = colors,
                monthNames = textStrings.monthNames,
                textStyles = textStyles,
                onMonthSelected = { month ->
                    selectedMonth = month
                    selectedDay = ensureValidDayNum(selectedDay, selectedMonth, selectedYear)
                },
                modifier = Modifier.weight(1f)
            )
            DayPicker(
                selectedDay = selectedDay,
                selectedMonth = selectedMonth,
                selectedYear = selectedYear,
                colors = colors,
                textStyles = textStyles,
                itemSpacingDp = itemSpacingDp,
                minSize = 48.dp,
                onDaySelected = { day -> selectedDay = day },
                modifier = Modifier.weight(1f)
            )
        }
        // when (selectedDateElement) {
        //     DateElement.DAY -> DayPicker(
        //         selectedDay = selectedDay,
        //         selectedMonth = selectedMonth,
        //         selectedYear = selectedYear,
        //         colors = colors,
        //         textStyles = textStyles,
        //         minSize = 48.dp,
        //         itemSpacingDp = itemSpacingDp,
        //         onDayClick = { day -> selectedDay = day }
        //     )
//
        //     DateElement.MONTH -> MonthPicker(
        //         selectedMonth = selectedMonth,
        //         itemSpacingDp = itemSpacingDp,
        //         colors = colors,
        //         monthNames = textStrings.monthNames,
        //         textStyles = textStyles,
        //         onMonthSelected = { month ->
        //             selectedMonth = month
        //             selectedDay = ensureValidDayNum(selectedDay, selectedMonth, selectedYear)
        //         }
        //     )
//
        //     DateElement.YEAR -> YearPicker(
        //         selectedYear = selectedYear,
        //         colors = colors,
        //         textStyles = textStyles,
        //         onYearSelected = { year ->
        //             selectedYear = year
        //             selectedDay = ensureValidDayNum(selectedDay, selectedMonth, selectedYear)
        //         }
        //     )
        // }
    }
}

@Composable
private fun DateElements(
    dateElementOrder: Triple<DateElement, DateElement, DateElement>,
    selectedDateElement: DateElement,
    selectedDay: Int?,
    selectedMonth: Month?,
    selectedYear: Int?,
    textStrings: ComposeDOBPickerTextStrings,
    colors: ComposeDOBPickerColors,
    textStyles: ComposeDOBPickerTextStyles,
    itemSpacingDp: Dp,
    onDateElementClick: (DateElement) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val dateElementList = dateElementOrder.toList()
        dateElementList.forEachIndexed { index, dateElement ->
            val text = when (dateElement) {
                DateElement.DAY -> selectedDay?.toString() ?: textStrings.dayTitleText
                DateElement.MONTH -> selectedMonth
                    ?.let { month -> textStrings.monthNames[month] }
                    ?: textStrings.monthTitleText

                DateElement.YEAR -> selectedYear?.toString() ?: textStrings.yearTitleText
            }
            SelectableTextBox(
                text = text,
                textColor = colors.dateElementTextColor,
                boxColor = colors.dateElementBoxColor,
                selected = selectedDateElement == dateElement,
                selectedTextColor = colors.dateElementSelectedTextColor,
                selectedBoxColor = colors.dateElementSelectedBoxColor,
                textStyle = textStyles.dateElementTextStyle,
                onClick = { onDateElementClick(dateElement) },
                modifier = Modifier.weight(weight = 1f)
            )
            if (index != dateElementList.lastIndex) Spacer(modifier = Modifier.width(itemSpacingDp))
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
    itemSpacingDp: Dp,
    minSize: Dp,
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
        textColor = colors.dateItemTextColor,
        selectedTextColor = colors.dateItemSelectedBoxColor,
        onItemSelected = { _, year -> onDaySelected(year) },
        modifier = modifier,
        numberOfDisplayedItems = 5
    )
    // LazyVerticalGrid(
    //     columns = GridCells.Adaptive(minSize),
    //     content = {
    //         (1 until numDays + 1).forEach { dayNum ->
    //             item(key = dayNum) {
    //                 SelectableTextBox(
    //                     text = dayNum.toString(),
    //                     textColor = colors.dateItemTextColor,
    //                     boxColor = colors.dateItemBoxColor,
    //                     selected = dayNum == selectedDay,
    //                     selectedBoxColor = colors.dateItemSelectedBoxColor,
    //                     selectedTextColor = colors.dateItemSelectedTextColor,
    //                     textStyle = textStyles.dateItemTextStyle,
    //                     onClick = { onDayClick(dayNum) },
    //                     modifier = Modifier.aspectRatio(1f),
    //                     paddingValues = PaddingValues(0.dp)
    //                 )
    //             }
    //         }
    //     },
    //     modifier = modifier,
    //     horizontalArrangement = Arrangement.spacedBy(itemSpacingDp),
    //     verticalArrangement = Arrangement.spacedBy(itemSpacingDp)
    // )
}

@Composable
private fun MonthPicker(
    selectedMonth: Month?,
    itemSpacingDp: Dp,
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
        textColor = colors.dateItemTextColor,
        getItemText = { item -> monthNames[item] ?: "" },
        selectedTextColor = colors.dateItemSelectedBoxColor,
        onItemSelected = { _, month -> onMonthSelected(month) },
        modifier = modifier,
        numberOfDisplayedItems = 5
    )

    // Column(modifier = modifier) {
    //     Month.values()
    //         .toList()
    //         .chunked(3)
    //         .forEach { months ->
    //             Row(modifier = Modifier.fillMaxWidth()) {
    //                 months.forEachIndexed { index, month ->
    //                     val selected = month == selectedMonth
    //                     SelectableTextBox(
    //                         text = monthNames[month] ?: month.name,
    //                         textColor = colors.dateItemTextColor,
    //                         boxColor = colors.dateItemBoxColor,
    //                         selected = selected,
    //                         selectedTextColor = colors.dateItemSelectedTextColor,
    //                         selectedBoxColor = colors.dateItemSelectedBoxColor,
    //                         textStyle = textStyles.dateItemTextStyle,
    //                         onClick = { onMonthClick(month) },
    //                         modifier = Modifier.weight(1f)
    //                     )
    //                     if (index != months.lastIndex) {
    //                         Spacer(modifier = Modifier.width(itemSpacingDp))
    //                     }
    //                 }
    //             }
    //             Spacer(modifier = Modifier.height(itemSpacingDp))
    //         }
    // }
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
        textColor = colors.dateItemTextColor,
        selectedTextColor = colors.dateItemSelectedBoxColor,
        onItemSelected = { _, year -> onYearSelected(year) },
        modifier = modifier,
        numberOfDisplayedItems = 5
    )
}

@Composable
private fun SelectableTextBox(
    text: String,
    textColor: Color,
    boxColor: Color,
    selected: Boolean,
    selectedBoxColor: Color,
    selectedTextColor: Color,
    textStyle: TextStyle,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    boxShape: Shape = RoundedCornerShape(4.dp),
    paddingValues: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
) {
    Box(
        modifier = modifier
            .clip(shape = boxShape)
            .background(color = if (selected) selectedBoxColor else boxColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            modifier = Modifier
                .wrapContentSize()
                .padding(paddingValues = paddingValues),
            style = textStyle,
            color = if (selected) selectedTextColor else textColor,
            textAlign = TextAlign.Center
        )
    }
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
    numberOfDisplayedItems: Int,
    onItemSelected: (index: Int, item: T) -> Unit,
    modifier: Modifier = Modifier
) {
    val itemHalfHeight = LocalDensity.current.run { itemHeight.toPx() / 2f }
    val parentHalfHeight = ((itemHalfHeight * 2) * numberOfDisplayedItems) / 2f
    var selectedIndex by rememberSaveable {
        mutableStateOf(items.indexOf(selectedItem))
    }
    val scrollState =
        rememberLazyListState(selectedIndex)

    LazyColumn(
        modifier = modifier
            .height(itemHeight * numberOfDisplayedItems),
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
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        val y = coordinates.positionInParent().y + itemHalfHeight
                        val isSelected =
                            (y > parentHalfHeight - itemHalfHeight && y < parentHalfHeight + itemHalfHeight)
                        if (isSelected && selectedIndex != index) {
                            onItemSelected(index, item)
                            selectedIndex = index
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                val indexDiff = (selectedIndex - index).absoluteValue
                val fontSize =
                    LocalDensity.current.run {
                        (textStyle.fontSize.toPx() - indexDiff * 2.sp.toPx()).coerceAtLeast(
                            14.sp.toPx()
                        ).toSp()
                    }
                val animatedTextStyle by animateTextStyleAsState(
                    targetValue = textStyle.copy(fontSize = fontSize)
                )
                Text(
                    text = getItemText(item),
                    style = animatedTextStyle,
                    color = if (selectedIndex == index) {
                        selectedTextColor
                    } else {
                        textColor
                    }
                )
            }
        }
        items(numberOfDisplayedItems / 2) {
            Box(modifier = Modifier.height(itemHeight))
        }
    }
}

@Composable
fun animateTextStyleAsState(
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
    DAY, MONTH, YEAR
}

enum class Month {
    JAN, FEB, MAR, APR, MAY, JUN, JUL, AUG, SEP, OCT, NOV, DEC
}

@Immutable
data class ComposeDOBPickerColors(
    val dateElementTextColor: Color = Color(0xFF5865F2),
    val dateElementBoxColor: Color = Color(0xFFF1F1F1),
    val dateElementSelectedTextColor: Color = Color(0xFFE8E8F6),
    val dateElementSelectedBoxColor: Color = Color(0xFF5865F2),
    val dateItemTextColor: Color = Color(0xFFA1A1A1),
    val dateItemBoxColor: Color = Color(0xFFF1F1F1),
    val dateItemSelectedTextColor: Color = Color(0xFFE8E8F6),
    val dateItemSelectedBoxColor: Color = Color(0xFF5865F2)
)

@Immutable
data class ComposeDOBPickerTextStrings(
    val dayTitleText: String = "Day",
    val monthTitleText: String = "Month",
    val yearTitleText: String = "Year",
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
    val dateElementTextStyle: TextStyle = TextStyle(
        fontSize = TextUnit(16f, TextUnitType.Sp),
        fontWeight = FontWeight.Medium,
        fontStyle = FontStyle.Normal
    ),
    val dateItemTextStyle: TextStyle = TextStyle(
        fontSize = TextUnit(14f, TextUnitType.Sp),
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Normal
    ),
    val yearItemTextStyle: TextStyle = TextStyle(
        fontSize = TextUnit(24f, TextUnitType.Sp),
        fontWeight = FontWeight.Medium,
        fontStyle = FontStyle.Normal
    )
)
