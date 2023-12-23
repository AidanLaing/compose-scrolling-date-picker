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
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.util.Calendar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Column(modifier = Modifier.background(color = Color.White)) {
                var dateOfBirth: DateOfBirth? by remember { mutableStateOf(null) }

                DateOfBirthPicker(
                    defaultListItem = { text, heightDp, _ ->
                        Box(
                            modifier = Modifier
                                .height(heightDp)
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = text,
                                modifier = Modifier.align(Alignment.Center),
                                color = Color.Black
                            )
                        }
                    },
                    dateOfBirthChanged = { newDateOfBirth -> dateOfBirth = newDateOfBirth },
                    maxYear = Calendar.getInstance().get(Calendar.YEAR),
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
            }
        }
    }
}