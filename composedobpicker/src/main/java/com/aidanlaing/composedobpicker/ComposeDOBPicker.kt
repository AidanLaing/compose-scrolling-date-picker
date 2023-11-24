package com.aidanlaing.composedobpicker

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp

@Composable
fun ComposeDOBPicker(
    modifier: Modifier = Modifier,
    spacingDp: Dp = 4.dp,
    dateElementOrder: Triple<DateElement, DateElement, DateElement> = Triple(
        DateElement.DAY,
        DateElement.MONTH,
        DateElement.YEAR
    ),
    colors: ComposeDOBPickerColors = ComposeDOBPickerColors(),
    dayTitleText: String = "Day",
    monthTitleText: String = "Month",
    yearTitleText: String = "Year",
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
    )
) {
    var selectedDateElement: DateElement by rememberSaveable { mutableStateOf(DateElement.DAY) }
    var selectedDay: Int? by rememberSaveable { mutableStateOf(null) }
    var selectedMonth: Month? by rememberSaveable { mutableStateOf(null) }
    var selectedYear: Int? by rememberSaveable { mutableStateOf(null) }
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val dateElementList = dateElementOrder.toList()
            dateElementList.forEachIndexed { index, dateElement ->
                val text = when (dateElement) {
                    DateElement.DAY -> selectedDay?.toString() ?: dayTitleText
                    DateElement.MONTH -> selectedMonth?.let { monthNames[it] } ?: monthTitleText
                    DateElement.YEAR -> selectedYear?.toString() ?: yearTitleText
                }
                SelectableTextBox(
                    text = text,
                    textColor = colors.dateElementTextColor,
                    boxColor = colors.dateElementBoxColor,
                    selected = selectedDateElement == dateElement,
                    selectedTextColor = colors.dateElementSelectedTextColor,
                    selectedBoxColor = colors.dateElementSelectedBoxColor,
                    onClick = { selectedDateElement = dateElement },
                    modifier = Modifier.weight(weight = 1f)
                )
                if (index != dateElementList.lastIndex) Spacer(modifier = Modifier.width(spacingDp))
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        when (selectedDateElement) {
            DateElement.DAY -> DayPicker()
            DateElement.MONTH -> MonthPicker(
                selectedMonth = selectedMonth,
                spacingDp = spacingDp,
                colors = colors,
                monthNames = monthNames,
                onMonthClick = { month -> selectedMonth = month }
            )

            DateElement.YEAR -> YearPicker()
        }
    }
}

@Composable
private fun DayPicker() {

}

@Composable
private fun MonthPicker(
    selectedMonth: Month?,
    spacingDp: Dp,
    colors: ComposeDOBPickerColors,
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
                            onClick = { onMonthClick(month) },
                            modifier = Modifier.weight(1f)
                        )
                        if (index != months.lastIndex) Spacer(modifier = Modifier.width(spacingDp))
                    }
                }
                Spacer(modifier = Modifier.height(spacingDp))
            }
    }
}

@Composable
private fun YearPicker() {

}

@Composable
private fun SelectableTextBox(
    text: String,
    textColor: Color,
    boxColor: Color,
    selected: Boolean,
    selectedBoxColor: Color,
    selectedTextColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    boxShape: Shape = RoundedCornerShape(4.dp),
    textStyle: TextStyle = TextStyle(
        fontSize = TextUnit(16f, TextUnitType.Sp),
        fontWeight = FontWeight.Medium,
        fontStyle = FontStyle.Normal
    ),
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
            color = if (selected) selectedTextColor else textColor
        )
    }
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
    val dateElementBoxColor: Color = Color(0xFFF1F2FF),
    val dateElementSelectedTextColor: Color = Color(0xFFE8E8F6),
    val dateElementSelectedBoxColor: Color = Color(0xFF5865F2),
    val dateItemTextColor: Color = Color(0xFFC1C1C1),
    val dateItemBoxColor: Color = Color(0xFFF5F5F5),
    val dateItemSelectedTextColor: Color = Color(0xFFE8E8F6),
    val dateItemSelectedBoxColor: Color = Color(0xFF5865F2)
)
