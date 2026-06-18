package com.vinberg88.blanketforandroid.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import android.provider.OpenableColumns
import com.vinberg88.blanketforandroid.model.Sound
import com.vinberg88.blanketforandroid.model.iconForName
import com.vinberg88.blanketforandroid.ui.components.SoundTile
import com.vinberg88.blanketforandroid.ui.theme.DarkSurfaceVariant
import com.vinberg88.blanketforandroid.viewmodel.BlanketViewModel

private val BOTTOM_BAR_HEIGHT = 72.dp
private val PLAY_BUTTON_SIZE = 46.dp
private val PLAY_ICON_SIZE = 28.dp
private val HORIZONTAL_PADDING = 4.dp
private val ADD_TILE_ICON_SIZE = 68.dp
private val ADD_TILE_ICON_INNER_SIZE = 32.dp
private val ADD_TILE_NAME_HEIGHT = 32.dp
private val ADD_TILE_PADDING = 4.dp
private val ADD_TILE_SPACING = 5.dp
private val CUSTOM_ICON_OPTIONS = listOf("music_note", "library_music", "audiotrack", "graphic_eq", "radio", "headphones")
private val SLEEP_TIMER_OPTIONS = listOf(15, 30, 60)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: BlanketViewModel) {
    val soundStates by viewModel.soundStates.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val allSounds by viewModel.allSounds.collectAsState()
    val masterVolume by viewModel.masterVolume.collectAsState()
    val sleepTimerMinutes by viewModel.sleepTimerMinutes.collectAsState()
    val context = LocalContext.current
    var showTimerDialog by remember { mutableStateOf(false) }
    var editingSound by remember { mutableStateOf<Sound?>(null) }
    var editName by remember { mutableStateOf("") }
    var editIconName by remember { mutableStateOf("music_note") }

    val soundPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            val displayName = context.contentResolver.query(
                uri, null, null, null, null
            )?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex >= 0 && cursor.moveToFirst()) {
                    cursor.getString(nameIndex)
                        .replace(Regex("\\.(mp3|wav|ogg|m4a|flac|aac)$", RegexOption.IGNORE_CASE), "")
                } else null
            } ?: "Custom Sound"
            viewModel.addCustomSound(uri, displayName)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Blanket") },
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
                modifier = Modifier.height(BOTTOM_BAR_HEIGHT)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Master volume",
                        modifier = Modifier.size(22.dp)
                    )
                    Slider(
                        value = masterVolume,
                        onValueChange = { viewModel.setMasterVolume(it) },
                        modifier = Modifier.width(104.dp),
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                            inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    FilledIconButton(
                        onClick = { viewModel.togglePlayPause() },
                        modifier = Modifier.size(PLAY_BUTTON_SIZE)
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            modifier = Modifier.size(PLAY_ICON_SIZE)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    IconButton(onClick = { showTimerDialog = true }) {
                        Icon(
                            imageVector = if (sleepTimerMinutes == null) Icons.Default.Timer else Icons.Default.TimerOff,
                            contentDescription = "Sleep timer"
                        )
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
                .padding(horizontal = HORIZONTAL_PADDING)
        ) {
            items(allSounds) { sound ->
                val state = soundStates[sound.id]
                SoundTile(
                    soundId = sound.id,
                    icon = sound.icon,
                    name = sound.displayName,
                    isEnabled = state?.isEnabled ?: false,
                    volume = state?.volume ?: 0.5f,
                    onToggle = { viewModel.toggleSound(sound.id) },
                    onVolumeChange = { volume -> viewModel.setSoundVolume(sound.id, volume) },
                    onEdit = if (sound.isCustom) {
                        {
                            editingSound = sound
                            editName = sound.displayName
                            editIconName = sound.iconName.ifBlank { "music_note" }
                        }
                    } else null
                )
            }

            // "Add Sound" tile
            item {
                Column(
                    modifier = Modifier.padding(ADD_TILE_PADDING),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(ADD_TILE_ICON_SIZE)
                            .clip(CircleShape)
                            .background(DarkSurfaceVariant)
                            .clickable {
                                soundPickerLauncher.launch(arrayOf("audio/*"))
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Sound",
                            modifier = Modifier.size(ADD_TILE_ICON_INNER_SIZE),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(ADD_TILE_SPACING))

                    Text(
                        text = "Add Sound",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        modifier = Modifier.height(ADD_TILE_NAME_HEIGHT)
                    )
                }
            }
        }
    }

    if (showTimerDialog) {
        AlertDialog(
            onDismissRequest = { showTimerDialog = false },
            title = { Text("Sleep Timer") },
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

    editingSound?.let { sound ->
        AlertDialog(
            onDismissRequest = { editingSound = null },
            title = { Text("Edit Sound") },
            text = {
                Column {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CUSTOM_ICON_OPTIONS.forEach { iconName ->
                            FilterChip(
                                selected = editIconName == iconName,
                                onClick = { editIconName = iconName },
                                label = {
                                    Icon(
                                        imageVector = iconForName(iconName),
                                        contentDescription = iconName,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.updateCustomSound(
                            sound.id,
                            editName.ifBlank { sound.displayName },
                            editIconName
                        )
                        editingSound = null
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                Row {
                    TextButton(
                        onClick = {
                            viewModel.removeCustomSound(sound.id)
                            editingSound = null
                        }
                    ) {
                        Text("Delete")
                    }
                    TextButton(onClick = { editingSound = null }) {
                        Text("Cancel")
                    }
                }
            }
        )
    }
}
