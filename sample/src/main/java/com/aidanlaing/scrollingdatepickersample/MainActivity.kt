package com.aidanlaing.scrollingdatepickersample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.aidanlaing.scrollingdatepicker.ScrollingDatePicker
import com.aidanlaing.scrollingdatepicker.ScrollingDatePickerDialog
import com.aidanlaing.scrollingdatepicker.ScrollingDatePickerProperties
import com.aidanlaing.scrollingdatepicker.ScrollingDatePickerUi
import com.aidanlaing.scrollingdatepicker.SelectedDate
import java.util.Calendar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Column(modifier = Modifier.verticalScroll(state = rememberScrollState())) {
                    var inlineSelectedDate: SelectedDate? by remember { mutableStateOf(null) }
                    var dialogSelectedDate: SelectedDate? by remember { mutableStateOf(null) }

                    InlineSample(
                        selectedDate = inlineSelectedDate,
                        dateChanged = { newDate -> inlineSelectedDate = newDate }
                    )

                    Divider()

                    DialogSample(
                        selectedDate = dialogSelectedDate,
                        dateConfirmed = { newDate -> dialogSelectedDate = newDate }
                    )

                    Divider()
                }
            }
        }
    }

    @Composable
    private fun InlineSample(
        selectedDate: SelectedDate?,
        dateChanged: (selectedDate: SelectedDate) -> Unit
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(24.dp)
                )
                .border(1.dp, color = DividerDefaults.color, shape = RoundedCornerShape(24.dp))
        ) {
            Text(
                text = selectedDate?.asText(pattern = "mmmm dz, yyyy") ?: "",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 16.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineMedium
            )

            Divider()

            ScrollingDatePicker(
                scrollingDatePickerUi = ScrollingDatePickerUi.Unified(
                    listItem = { text, heightDp, _ ->
                        ScrollingDatePickerItem(text = text, heightDp = heightDp)
                    }
                ),
                maxYear = Calendar.getInstance().get(Calendar.YEAR),
                dateChanged = dateChanged,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    @Composable
    private fun DialogSample(
        selectedDate: SelectedDate?,
        dateConfirmed: (SelectedDate) -> Unit
    ) {
        var showScrollingDatePickerDialog: Boolean by remember { mutableStateOf(false) }
        var dialogSelectedDate: SelectedDate? by remember { mutableStateOf(null) }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(color = MaterialTheme.colorScheme.background)
                .clickable { showScrollingDatePickerDialog = true },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Select Date",
                modifier = Modifier.padding(start = 16.dp),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = selectedDate?.asText(pattern = "mmmm dz, yyyy") ?: "",
                modifier = Modifier.padding(end = 16.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        if (showScrollingDatePickerDialog) {
            ScrollingDatePickerDialog(
                scrollingDatePickerUi = ScrollingDatePickerUi.Unified(
                    listItem = { text, heightDp, _ ->
                        ScrollingDatePickerItem(text = text, heightDp = heightDp)
                    }
                ),
                maxYear = Calendar.getInstance().get(Calendar.YEAR),
                backgroundColor = MaterialTheme.colorScheme.surface,
                dateChanged = { newDate -> dialogSelectedDate = newDate },
                onDismissRequest = { showScrollingDatePickerDialog = false },
                scrollingDatePickerProperties = ScrollingDatePickerProperties(
                    defaultSelectedDay = dialogSelectedDate?.day ?: 1,
                    defaultSelectedMonth = dialogSelectedDate?.month ?: 0,
                    defaultSelectedYear = dialogSelectedDate?.year ?: 2000
                ),
                footerContent = {
                    DialogFooterContent(
                        onDismiss = { showScrollingDatePickerDialog = false },
                        onConfirm = {
                            showScrollingDatePickerDialog = false
                            dialogSelectedDate?.let { newDate -> dateConfirmed(newDate) }
                        }
                    )
                }
            )
        }
    }

    @Composable
    private fun DialogFooterContent(
        onDismiss: () -> Unit,
        onConfirm: () -> Unit
    ) {
        Divider()
        Row(modifier = Modifier.padding(end = 16.dp, top = 8.dp, bottom = 8.dp)) {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Text(text = "Dismiss")
            }
            TextButton(onClick = onConfirm) {
                Text(text = "Confirm")
            }
        }
    }

    @Composable
    private fun ScrollingDatePickerItem(text: String, heightDp: Dp) {
        Box(
            modifier = Modifier
                .height(heightDp)
                .fillMaxWidth()
        ) {
            Text(
                text = text,
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}