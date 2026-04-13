package com.helpful.healingnotes.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.helpful.healingnotes.data.solfeggioPresets
import com.helpful.healingnotes.ui.components.BinauralControl
import com.helpful.healingnotes.ui.components.FrequencyInput
import com.helpful.healingnotes.ui.components.PresetBottomSheet
import com.helpful.healingnotes.viewmodel.ToneViewModel

@Composable
fun ToneScreen(vm: ToneViewModel, windowSizeClass: WindowSizeClass? = null) {
    val state = vm.uiState
    val isExpanded = windowSizeClass?.widthSizeClass == WindowWidthSizeClass.Expanded

    // 🧘 Zen Gradient Background
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.surface
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Solfeggio Generator",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(Modifier.height(24.dp))

        if (isExpanded) {
            // 📖 Tablet / Landscape Layout: Two Columns
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Left Column: Solfeggio
                Column(modifier = Modifier.weight(1f)) {
                    SolfeggioSection(state.baseFreq, vm)
                }

                // Right Column: Binaural
                Column(modifier = Modifier.weight(1f)) {
                    BinauralSection(state.binauralStart, state.swapEars, vm)
                }
            }
        } else {
            // 📱 Phone Layout: Vertical Stack
            Column(modifier = Modifier.widthIn(max = 600.dp)) {
                SolfeggioSection(state.baseFreq, vm)
                Spacer(Modifier.height(20.dp))
                BinauralSection(state.binauralStart, state.swapEars, vm)
            }
        }

        Spacer(Modifier.height(32.dp))

        // 🔘 Action Buttons (Centered and width-constrained)
        Row(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .fillMaxWidth()
        ) {
            Button(
                onClick = { vm.play() },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("PLAY")
            }
            
            Spacer(Modifier.width(12.dp))
            
            Button(
                onClick = { vm.stop() },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                )
            ) {
                Icon(Icons.Default.Stop, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("STOP")
            }
        }
        
        Spacer(Modifier.height(40.dp))
    }

    if (vm.showPresetSheet) {
        PresetBottomSheet(
            presets = solfeggioPresets,
            favorites = vm.favorites.value,
            onSelect = {
                vm.setFrequency(it.freq.toDouble())
                vm.showPresetSheet = false
            },
            onToggleFavorite = { vm.toggleFavorite(it) },
            onDismiss = { vm.showPresetSheet = false }
        )
    }
}

@Composable
private fun SolfeggioSection(baseFreq: Double, vm: ToneViewModel) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Column(Modifier.padding(16.dp)) {
            FrequencyInput(
                value = baseFreq,
                onChange = { vm.setFrequency(it) },
                onShowPresets = { vm.showPresetSheet = true }
            )
        }
    }
}

@Composable
private fun BinauralSection(binauralValue: Double, swapEars: Boolean, vm: ToneViewModel) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Column(Modifier.padding(16.dp)) {
            BinauralControl(
                value = binauralValue,
                swapEars = swapEars,
                onSwapChange = {
                    vm.uiState = vm.uiState.copy(swapEars = it)
                },
                onChange = {
                    vm.uiState = vm.uiState.copy(
                        binauralStart = it,
                        binauralEnd = it
                    )
                }
            )
        }
    }
}