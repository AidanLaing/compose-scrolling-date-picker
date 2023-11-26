package com.aidanlaing.composedobpicker

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp

// https://www.behance.net/gallery/175723981/Date-Picker?tracking_source=search_projects|date+of+birth+picker&l=2
// TODO day picker
// TODO year picker
// TODO invalid day picker handling when month or year changed to leap year
// TODO max and min date params
// TODO animations
// TODO landscape mode / tablet
// TODO auto advance
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
        DateElement.DAY,
        DateElement.MONTH,
        DateElement.YEAR
    ),
    colors: ComposeDOBPickerColors = ComposeDOBPickerColors(),
    textStyles: ComposeDOBPickerTextStyles = ComposeDOBPickerTextStyles(),
    textStrings: ComposeDOBPickerTextStrings = ComposeDOBPickerTextStrings()
) {
    var selectedDateElement: DateElement by rememberSaveable { mutableStateOf(DateElement.DAY) }
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

        when (selectedDateElement) {
            DateElement.DAY -> DayPicker(
                selectedDay = selectedDay,
                selectedMonth = selectedMonth,
                selectedYear = selectedYear,
                colors = colors,
                textStyles = textStyles,
                minSize = 48.dp,
                itemSpacingDp = itemSpacingDp,
                onDayClick = { day -> selectedDay = day }
            )

            DateElement.MONTH -> MonthPicker(
                selectedMonth = selectedMonth,
                itemSpacingDp = itemSpacingDp,
                colors = colors,
                monthNames = textStrings.monthNames,
                textStyles = textStyles,
                onMonthClick = { month ->
                    selectedMonth = month
                    selectedDay = ensureValidDayNum(selectedDay, selectedMonth, selectedYear)
                }
            )

            DateElement.YEAR -> YearPicker(
                selectedYear = selectedYear,
                onYearClick = { year ->
                    selectedYear = year
                    selectedDay = ensureValidDayNum(selectedDay, selectedMonth, selectedYear)
                }
            )
        }
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
    onDayClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val numDays: Int = calculateNumDaysInMonth(selectedMonth, selectedYear)
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize),
        content = {
            (1 until numDays + 1).forEach { dayNum ->
                item(key = dayNum) {
                    SelectableTextBox(
                        text = dayNum.toString(),
                        textColor = colors.dateItemTextColor,
                        boxColor = colors.dateItemBoxColor,
                        selected = dayNum == selectedDay,
                        selectedBoxColor = colors.dateItemSelectedBoxColor,
                        selectedTextColor = colors.dateItemSelectedTextColor,
                        textStyle = textStyles.dateItemTextStyle,
                        onClick = { onDayClick(dayNum) },
                        modifier = Modifier.aspectRatio(1f),
                        paddingValues = PaddingValues(0.dp)
                    )
                }
            }
        },
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(itemSpacingDp),
        verticalArrangement = Arrangement.spacedBy(itemSpacingDp)
    )
}

@Composable
private fun MonthPicker(
    selectedMonth: Month?,
    itemSpacingDp: Dp,
    colors: ComposeDOBPickerColors,
    textStyles: ComposeDOBPickerTextStyles,
    monthNames: Map<Month, String>,
    onMonthClick: (Month) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Month.values()
            .toList()
            .chunked(3)
            .forEach { months ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    months.forEachIndexed { index, month ->
                        val selected = month == selectedMonth
                        SelectableTextBox(
                            text = monthNames[month] ?: month.name,
                            textColor = colors.dateItemTextColor,
                            boxColor = colors.dateItemBoxColor,
                            selected = selected,
                            selectedTextColor = colors.dateItemSelectedTextColor,
                            selectedBoxColor = colors.dateItemSelectedBoxColor,
                            textStyle = textStyles.dateItemTextStyle,
                            onClick = { onMonthClick(month) },
                            modifier = Modifier.weight(1f)
                        )
                        if (index != months.lastIndex) Spacer(
                            modifier = Modifier.width(
                                itemSpacingDp
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(itemSpacingDp))
            }
    }
}

@Composable
private fun YearPicker(
    selectedYear: Int?,
    onYearClick: (Int) -> Unit
) {

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
)
