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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import android.provider.OpenableColumns
import com.vinberg88.blanketforandroid.ui.components.SoundTile
import com.vinberg88.blanketforandroid.ui.theme.DarkSurfaceVariant
import com.vinberg88.blanketforandroid.viewmodel.BlanketViewModel

private val BOTTOM_BAR_HEIGHT = 80.dp
private val PLAY_BUTTON_SIZE = 56.dp
private val PLAY_ICON_SIZE = 32.dp
private val HORIZONTAL_PADDING = 8.dp
private val ADD_TILE_ICON_SIZE = 72.dp
private val ADD_TILE_ICON_INNER_SIZE = 36.dp
private val ADD_TILE_NAME_HEIGHT = 40.dp
private val ADD_TILE_PADDING = 8.dp
private val ADD_TILE_SPACING = 8.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: BlanketViewModel) {
    val soundStates by viewModel.soundStates.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val allSounds by viewModel.allSounds.collectAsState()
    val context = LocalContext.current

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
                }
            }
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = paddingValues,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = HORIZONTAL_PADDING)
        ) {
            items(allSounds) { sound ->
                val state = soundStates[sound.id]
                SoundTile(
                    icon = sound.icon,
                    name = sound.displayName,
                    isEnabled = state?.isEnabled ?: false,
                    volume = state?.volume ?: 0.5f,
                    onToggle = { viewModel.toggleSound(sound.id) },
                    onVolumeChange = { volume -> viewModel.setSoundVolume(sound.id, volume) },
                    onDelete = if (sound.isCustom) {
                        { viewModel.removeCustomSound(sound.id) }
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
}
