package com.example.playit.features.equalizer

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EqualizerScreen(viewModel: EqualizerViewModel = hiltViewModel(), onBack: () -> Unit = {}) {
    val enabled by viewModel.enabled.collectAsState()
    val presets = remember { viewModel.presets() }
    val bandCount = remember { viewModel.bandCount() }
    val (minLevel, maxLevel) = remember { viewModel.bandLevelRange() }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Equalizer") })
    }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Enabled", color = MaterialTheme.colorScheme.onBackground)
                Spacer(Modifier.width(12.dp))
                Switch(
                    checked = enabled,
                    onCheckedChange = viewModel::setEnabled,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                        checkedTrackColor = MaterialTheme.colorScheme.primary,
                        uncheckedThumbColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            }

            if (presets.isNotEmpty()) {
                var selectedIndex by remember { mutableStateOf(0) }
                Text("Preset", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onBackground)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    presets.forEachIndexed { index, name ->
                        FilterChip(
                            selected = selectedIndex == index,
                            onClick = {
                                selectedIndex = index
                                viewModel.applyPresetIndex(index)
                            },
                            label = { Text(name) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }
            }

            repeat(bandCount) { band ->
                var slider by remember { mutableStateOf(0f) }
                Column(modifier = Modifier.padding(vertical = 2.dp)) {
                    Text(
                        "Band ${band + 1}",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.labelLarge
                    )
                    com.example.playit.ui.components.PlayItSliderPro(
                        value = slider,
                        onValueChange = {
                            slider = it
                            val level = (minLevel + (maxLevel - minLevel) * it).toInt()
                            viewModel.setBandLevel(band, level)
                        },
                        valueRange = 0f..1f,
                        bufferedFraction = null,
                        trackHeight = 4.dp,
                        showCenterTick = true
                    )
                }
            }

            Text("Bass Boost", color = MaterialTheme.colorScheme.onBackground)
            var bass by remember { mutableStateOf(0f) }
            com.example.playit.ui.components.PlayItSliderPro(
                value = bass,
                onValueChange = {
                    bass = it
                    viewModel.setBassBoost((it * 1000).toInt())
                },
                valueRange = 0f..1f,
                bufferedFraction = null,
                trackHeight = 4.dp
            )

            Text("Virtualizer", color = MaterialTheme.colorScheme.onBackground)
            var virt by remember { mutableStateOf(0f) }
            com.example.playit.ui.components.PlayItSliderPro(
                value = virt,
                onValueChange = {
                    virt = it
                    viewModel.setVirtualizer((it * 1000).toInt())
                },
                valueRange = 0f..1f,
                bufferedFraction = null,
                trackHeight = 4.dp
            )
        }
    }
}


