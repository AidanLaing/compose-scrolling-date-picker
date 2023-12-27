package com.aidanlaing.composedobpicker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun DateOfBirthPickerDialog(
    dateOfBirthPickerUi: DateOfBirthPickerUi,
    maxYear: Int,
    backgroundColor: Color,
    buttonFooterContent: @Composable ColumnScope.(onConfirmClick: () -> Unit, onDismissClick: () -> Unit) -> Unit,
    onDateConfirmed: (DateOfBirth?) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    dateOfBirthPickerProperties: DateOfBirthPickerProperties = DateOfBirthPickerProperties(),
    dialogProperties: DialogProperties = DialogProperties(),
    backgroundShape: Shape = RoundedCornerShape(16.dp),
    horizontalAlignment: Alignment.Horizontal = Alignment.End,
    headerContent: (@Composable ColumnScope.() -> Unit)? = null,
    dateOfBirthChanged: ((DateOfBirth) -> Unit)? = null
) {
    Dialog(onDismissRequest = onDismissRequest, properties = dialogProperties) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(color = backgroundColor, shape = backgroundShape),
            horizontalAlignment = horizontalAlignment
        ) {
            headerContent?.invoke(this)

            var dateOfBirth: DateOfBirth? by remember { mutableStateOf(null) }
            DateOfBirthPicker(
                dateOfBirthPickerUi = dateOfBirthPickerUi,
                maxYear = maxYear,
                dateOfBirthChanged = { newDateOfBirth ->
                    dateOfBirth = newDateOfBirth
                    dateOfBirthChanged?.invoke(newDateOfBirth)
                },
                modifier = Modifier.fillMaxWidth(),
                properties = dateOfBirthPickerProperties
            )

            buttonFooterContent(
                {
                    onDateConfirmed(dateOfBirth)
                    onDismissRequest()
                },
                onDismissRequest
            )
        }
    }
}