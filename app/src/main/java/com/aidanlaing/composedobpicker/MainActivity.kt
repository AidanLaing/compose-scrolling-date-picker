package com.aidanlaing.composedobpicker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            var updateCount by remember { mutableStateOf(0) }
            var dateText by remember { mutableStateOf("") }

            Column(modifier = Modifier.background(color = Color.White)) {
                ComposeDOBPicker(
                    modifier = Modifier
                        .fillMaxWidth(),
                    onDateChanged = { day, month, year ->
                        updateCount += 1
                        dateText = "${month.name} $day, $year Updated: $updateCount"
                    }
                )

                Text(
                    text = dateText,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 24.dp),
                    textAlign = TextAlign.Center,
                    color = Color.Black
                )
            }
        }
    }
}
