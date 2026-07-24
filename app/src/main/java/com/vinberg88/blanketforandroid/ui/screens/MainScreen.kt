package com.vinberg88.blanketforandroid.ui.screens

import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vinberg88.blanketforandroid.BuildConfig
import com.vinberg88.blanketforandroid.ui.components.SoundTile
import com.vinberg88.blanketforandroid.viewmodel.BlanketViewModel

private val PRESETS = listOf("Default", "Nature", "Focus", "Sleep")
private val SLEEP_TIMER_OPTIONS = listOf(15, 30, 60)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: BlanketViewModel) {
    val soundStates by viewModel.soundStates.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val allSounds by viewModel.allSounds.collectAsState()
    val masterVolume by viewModel.masterVolume.collectAsState()
    val selectedPreset by viewModel.selectedPreset.collectAsState()
    val sleepTimerMinutes by viewModel.sleepTimerMinutes.collectAsState()
    val context = LocalContext.current

    var showPresetMenu by remember { mutableStateOf(false) }
    var showOverflowMenu by remember { mutableStateOf(false) }
    var showTimerDialog by remember { mutableStateOf(false) }
    var showMasterVolumeDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    val soundPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            val displayName = context.contentResolver.query(
                uri, null, null, null, null
            )?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex >= 0 && cursor.moveToFirst()) cursor.getString(nameIndex) else null
            }?.replace(
                Regex("\\.(mp3|wav|ogg|m4a|flac|aac)$", RegexOption.IGNORE_CASE),
                ""
            ) ?: "Custom Sound"
            viewModel.addCustomSound(uri, displayName)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Blanket",
                            modifier = Modifier.align(Alignment.Center),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                },
                navigationIcon = {
                    Box {
                        TextButton(onClick = { showPresetMenu = true }) {
                            Text(selectedPreset, color = MaterialTheme.colorScheme.onSurface)
                            Text("⌄", modifier = Modifier.padding(start = 5.dp))
                        }
                        DropdownMenu(
                            expanded = showPresetMenu,
                            onDismissRequest = { showPresetMenu = false }
                        ) {
                            PRESETS.forEach { preset ->
                                DropdownMenuItem(
                                    text = { Text(preset) },
                                    onClick = {
                                        viewModel.applyPreset(preset)
                                        showPresetMenu = false
                                    }
                                )
                            }
                        }
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { showOverflowMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Open menu")
                        }
                        DropdownMenu(
                            expanded = showOverflowMenu,
                            onDismissRequest = { showOverflowMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Add custom sound") },
                                leadingIcon = { Icon(Icons.Default.Add, contentDescription = null) },
                                onClick = {
                                    showOverflowMenu = false
                                    soundPickerLauncher.launch(arrayOf("audio/*"))
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Master volume") },
                                leadingIcon = { Icon(Icons.Default.VolumeUp, contentDescription = null) },
                                onClick = {
                                    showOverflowMenu = false
                                    showMasterVolumeDialog = true
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        sleepTimerMinutes?.let { "Sleep timer: $it min" }
                                            ?: "Sleep timer"
                                    )
                                },
                                leadingIcon = { Icon(Icons.Default.Timer, contentDescription = null) },
                                onClick = {
                                    showOverflowMenu = false
                                    showTimerDialog = true
                                }
                            )
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("About Blanket") },
                                leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
                                onClick = {
                                    showOverflowMenu = false
                                    showAboutDialog = true
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.height(64.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = viewModel::toggleMasterMute) {
                        Icon(
                            imageVector = if (masterVolume <= 0.01f) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                            contentDescription = if (masterVolume <= 0.01f) "Unmute" else "Mute"
                        )
                    }

                    FilledIconButton(
                        onClick = viewModel::togglePlayPause,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause all sounds" else "Play selected sounds",
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    IconButton(onClick = { showOverflowMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Open controls")
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            contentPadding = paddingValues,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp)
        ) {
            items(allSounds, key = { it.id }) { sound ->
                val state = soundStates[sound.id]
                SoundTile(
                    soundId = sound.id,
                    icon = sound.icon,
                    name = sound.displayName,
                    isEnabled = state?.isEnabled ?: false,
                    volume = state?.volume ?: 0.45f,
                    onToggle = { viewModel.toggleSound(sound.id) },
                    onVolumeChange = { viewModel.setSoundVolume(sound.id, it) }
                )
            }
        }
    }

    if (showMasterVolumeDialog) {
        AlertDialog(
            onDismissRequest = { showMasterVolumeDialog = false },
            title = { Text("Master volume") },
            text = {
                Column {
                    Text("${(masterVolume * 100).toInt()}%")
                    Slider(value = masterVolume, onValueChange = viewModel::setMasterVolume)
                }
            },
            confirmButton = {
                TextButton(onClick = { showMasterVolumeDialog = false }) { Text("Done") }
            }
        )
    }

    if (showTimerDialog) {
        AlertDialog(
            onDismissRequest = { showTimerDialog = false },
            title = { Text("Sleep timer") },
            text = {
                Column {
                    SLEEP_TIMER_OPTIONS.forEach { minutes ->
                        TextButton(
                            onClick = {
                                viewModel.setSleepTimer(minutes)
                                showTimerDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("$minutes minutes")
                        }
                    }
                    if (sleepTimerMinutes != null) {
                        TextButton(
                            onClick = {
                                viewModel.cancelSleepTimer()
                                showTimerDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Cancel timer")
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("About Blanket") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Blanket for Android ${BuildConfig.VERSION_NAME}")
                    Text("Mix ambient sounds for focus, sleep and calm.")
                    Text("Android version by Mattias Vinberg.")
                    Text("Inspired by Blanket by Rafael Mardojai CM and contributors.")
                    Text("Sound credits: alex36917, Digifish music, felix.blume, Luftrum, gluckose, kvgarlic, Lisa Redfern, SDLx, Falcet, gezortenplotz, stephan, ezwa, Jorge Stolfi and Omegatron.")
                    Text("Licenses: CC BY, CC BY-SA, CC0 and public domain. Full source links are in SOUNDS_LICENSING.md.")
                    Text("No ads, accounts, analytics or tracking.")
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) { Text("OK") }
            }
        )
    }
}
