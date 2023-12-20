package com.aidanlaing.composedobpicker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import java.util.Calendar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Column(modifier = Modifier.background(color = Color.White)) {
                ComposeDOBPicker(
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
                    onDateChanged = { _, _, _ ->
                    },
                    maxYear = Calendar.getInstance().get(Calendar.YEAR),
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }
    }
}
