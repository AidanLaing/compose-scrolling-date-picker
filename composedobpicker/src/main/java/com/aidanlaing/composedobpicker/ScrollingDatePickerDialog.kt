package com.aidanlaing.composedobpicker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun ScrollingDatePickerDialog(
    scrollingDatePickerUi: ScrollingDatePickerUi,
    maxYear: Int,
    backgroundColor: Color,
    dateChanged: (SelectedDate) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    scrollingDatePickerProperties: ScrollingDatePickerProperties = ScrollingDatePickerProperties(),
    dialogProperties: DialogProperties = DialogProperties(),
    backgroundShape: Shape = RoundedCornerShape(16.dp),
    horizontalAlignment: Alignment.Horizontal = Alignment.End,
    headerContent: (@Composable ColumnScope.() -> Unit)? = null,
    footerContent: (@Composable ColumnScope.() -> Unit)? = null
) {
    Dialog(onDismissRequest = onDismissRequest, properties = dialogProperties) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(color = backgroundColor, shape = backgroundShape),
            horizontalAlignment = horizontalAlignment
        ) {
            headerContent?.invoke(this)

            ScrollingDatePicker(
                scrollingDatePickerUi = scrollingDatePickerUi,
                maxYear = maxYear,
                dateChanged = dateChanged,
                modifier = Modifier.fillMaxWidth(),
                properties = scrollingDatePickerProperties
            )

            footerContent?.invoke(this)
        }
    }
}