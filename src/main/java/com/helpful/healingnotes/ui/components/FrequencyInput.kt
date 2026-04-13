package com.helpful.healingnotes.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.helpful.healingnotes.util.formatHz
import com.helpful.healingnotes.util.snapSolfeggio

@Composable
fun FrequencyInput(
    value: Double,
    onChange: (Double) -> Unit,
    onShowPresets: () -> Unit
) {
    var text by remember { mutableStateOf(formatHz(value)) }

    // Sync text field when value changes externally (e.g. from preset selection)
    LaunchedEffect(value) {
        val formattedValue = formatHz(value)
        if (text != formattedValue) {
            text = formattedValue
        }
    }

    Column {

        Row(verticalAlignment = Alignment.CenterVertically) {

            // 📝 Text input
            OutlinedTextField(
                value = text,
                onValueChange = { input ->
                    text = input
                    input.toDoubleOrNull()?.let {
                        // Allow manual typing of any decimal, but could snap on focus loss if desired.
                        // For now, we update the engine with the typed value.
                        onChange(it)
                    }
                },
                label = { Text("Frequency (Hz)") },
                modifier = Modifier.weight(1f),
                trailingIcon = {
                    IconButton(onClick = onShowPresets) {
                        Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Show presets")
                    }
                }
            )
        }

        Spacer(Modifier.height(12.dp))

        // 🎚 Slider Container with Border
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                    RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Slider(
                value = value.toFloat(),
                onValueChange = {
                    val snapped = snapSolfeggio(it.toDouble())
                    onChange(snapped)
                    text = formatHz(snapped)
                },
                valueRange = 7.83f..1000f
            )
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Tap the list icon for presets", 
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}