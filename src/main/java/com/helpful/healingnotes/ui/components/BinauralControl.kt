package com.helpful.healingnotes.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
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
import com.helpful.healingnotes.data.binauralPresets
import com.helpful.healingnotes.util.formatHz
import com.helpful.healingnotes.util.snapBinaural

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BinauralControl(
    value: Double,
    swapEars: Boolean,
    onChange: (Double) -> Unit,
    onSwapChange: (Boolean) -> Unit
) {
    var text by remember { mutableStateOf(formatHz(value)) }

    // Sync text field when value changes externally (e.g. from slider)
    LaunchedEffect(value) {
        val formattedValue = formatHz(value)
        if (text != formattedValue) {
            text = formattedValue
        }
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Binaural Presets",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Swap Ears",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.width(8.dp))
                Switch(
                    checked = swapEars,
                    onCheckedChange = onSwapChange
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        // 🌊 FlowRow automatically wraps chips to the next line if they don't fit
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            binauralPresets.forEach { preset ->
                AssistChip(
                    onClick = {
                        onChange(preset.hz)
                        text = formatHz(preset.hz)
                    },
                    label = { Text("${preset.name} (${preset.hz} Hz)") }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = text,
                onValueChange = { input ->
                    text = input
                    input.toDoubleOrNull()?.let {
                        onChange(it)
                    }
                },
                label = { Text("Binaural Offset (Hz)") },
                modifier = Modifier.weight(1f),
                trailingIcon = {
                    Icon(Icons.Filled.MusicNote, contentDescription = null)
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
                    val snapped = snapBinaural(it.toDouble())
                    onChange(snapped)
                    text = formatHz(snapped)
                },
                valueRange = 0f..40f
            )
        }
    }
}
