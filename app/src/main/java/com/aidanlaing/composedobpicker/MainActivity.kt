package com.aidanlaing.composedobpicker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
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
import java.util.Calendar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Column(modifier = Modifier.background(color = MaterialTheme.colorScheme.background)) {
                    var dateOfBirth: DateOfBirth? by remember { mutableStateOf(null) }

                    InlineSample(dateOfBirthChanged = { newDateOfBirth -> dateOfBirth = newDateOfBirth })

                    Divider()

                    DialogSample(
                        onDateOfBirthConfirmed = { newDateOfBirth -> dateOfBirth = newDateOfBirth }
                    )

                    Divider()

                    Text(
                        text = dateOfBirth?.asText(pattern = "mmmm dz, yyyy") ?: "",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }

    @Composable
    private fun InlineSample(
        dateOfBirthChanged: (dateOfBirth: DateOfBirth) -> Unit
    ) {
        DateOfBirthPicker(
            dateOfBirthPickerUi = DateOfBirthPickerUi.Unified(
                listItem = { text, heightDp, _ ->
                    DateOfBirthPickerItem(text = text, heightDp = heightDp)
                }
            ),
            maxYear = Calendar.getInstance().get(Calendar.YEAR),
            dateOfBirthChanged = dateOfBirthChanged,
            modifier = Modifier.fillMaxWidth()
        )
    }

    @Composable
    private fun ColumnScope.DialogSample(
        onDateOfBirthConfirmed: (DateOfBirth) -> Unit
    ) {
        var showDateOfBirthPickerDialog: Boolean by remember { mutableStateOf(false) }
        var dialogDateOfBirth: DateOfBirth? by remember { mutableStateOf(null) }

        Button(
            onClick = { showDateOfBirthPickerDialog = true },
            modifier = Modifier
                .padding(24.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Show Picker Dialog")
        }

        if (showDateOfBirthPickerDialog) {
            DateOfBirthPickerDialog(
                dateOfBirthPickerUi = DateOfBirthPickerUi.Unified(
                    listItem = { text, heightDp, _ ->
                        DateOfBirthPickerItem(text = text, heightDp = heightDp)
                    }
                ),
                maxYear = Calendar.getInstance().get(Calendar.YEAR),
                backgroundColor = MaterialTheme.colorScheme.surface,
                dateOfBirthChanged = { newDateOfBirth -> dialogDateOfBirth = newDateOfBirth },
                onDismissRequest = { showDateOfBirthPickerDialog = false },
                dateOfBirthPickerProperties = DateOfBirthPickerProperties(
                    defaultSelectedDay = dialogDateOfBirth?.day ?: 1,
                    defaultSelectedMonth = dialogDateOfBirth?.month ?: 0,
                    defaultSelectedYear = dialogDateOfBirth?.year ?: 2000
                ),
                footerContent = {
                    Divider()
                    Row(modifier = Modifier.padding(end = 16.dp, top = 8.dp, bottom = 8.dp)) {
                        TextButton(
                            onClick = { showDateOfBirthPickerDialog = false },
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(text = "Dismiss")
                        }
                        TextButton(
                            onClick = {
                                showDateOfBirthPickerDialog = false
                                dialogDateOfBirth?.let { newDateOfBirth -> onDateOfBirthConfirmed(newDateOfBirth) }
                            }
                        ) {
                            Text(text = "Confirm")
                        }
                    }
                }
            )
        }
    }

    @Composable
    private fun DateOfBirthPickerItem(text: String, heightDp: Dp) {
        Box(
            modifier = Modifier
                .height(heightDp)
                .fillMaxWidth()
        ) {
            Text(
                text = text,
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}