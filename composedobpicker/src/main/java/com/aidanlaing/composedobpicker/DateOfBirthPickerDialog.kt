package com.aidanlaing.composedobpicker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun DateOfBirthPickerDialog(
    dateOfBirthPickerUi: DateOfBirthPickerUi,
    maxYear: Int,
    dateOfBirthChanged: (dateOfBirth: DateOfBirth) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    dateOfBirthPickerProperties: DateOfBirthPickerProperties = DateOfBirthPickerProperties(),
    dialogProperties: DialogProperties = DialogProperties()
) {
    Dialog(onDismissRequest = onDismissRequest, properties = dialogProperties) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(color = Color.White, shape = RoundedCornerShape(16.dp))
        ) {
            DateOfBirthPicker(
                dateOfBirthPickerUi,
                maxYear,
                dateOfBirthChanged,
                Modifier.fillMaxWidth(),
                dateOfBirthPickerProperties
            )
        }
    }
}