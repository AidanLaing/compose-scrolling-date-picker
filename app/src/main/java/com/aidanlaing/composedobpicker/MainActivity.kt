package com.aidanlaing.composedobpicker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.window.DialogProperties
import java.util.Calendar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {

                var dateOfBirth: DateOfBirth? by remember { mutableStateOf(null) }
                var showDateOfBirthPickerDialog: Boolean by remember { mutableStateOf(false) }

                Column(modifier = Modifier.background(color = MaterialTheme.colorScheme.background)) {

                    DateOfBirthPicker(
                        dateOfBirthPickerUi = DateOfBirthPickerUi.Unified(
                            listItem = { text, heightDp, _ ->
                                DateOfBirthPickerItem(text = text, heightDp = heightDp)
                            }
                        ),
                        maxYear = Calendar.getInstance().get(Calendar.YEAR),
                        dateOfBirthChanged = { newDateOfBirth -> dateOfBirth = newDateOfBirth },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Divider()

                    Text(
                        text = dateOfBirth?.asText(pattern = "mmmm dz, yyyy") ?: "",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        textAlign = TextAlign.Center
                    )

                    Divider()

                    Button(
                        onClick = { showDateOfBirthPickerDialog = true },
                        modifier = Modifier
                            .padding(24.dp)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        Text(text = "Show Picker Dialog")
                    }
                }

                if (showDateOfBirthPickerDialog) {
                    DateOfBirthPickerDialog(
                        dateOfBirthPickerUi = DateOfBirthPickerUi.Unified(
                            listItem = { text, heightDp, _ ->
                                DateOfBirthPickerItem(text = text, heightDp = heightDp)
                            }
                        ),
                        maxYear = Calendar.getInstance().get(Calendar.YEAR),
                        dateOfBirthChanged = { newDateOfBirth -> dateOfBirth = newDateOfBirth },
                        onDismissRequest = { showDateOfBirthPickerDialog = false },
                        dialogProperties = DialogProperties(),
                        backgroundColor = MaterialTheme.colorScheme.surface
                    )
                }
            }
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