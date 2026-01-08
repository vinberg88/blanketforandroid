package com.vinberg88.blanketforandroid.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vinberg88.blanketforandroid.ui.theme.BlueAccent
import com.vinberg88.blanketforandroid.ui.theme.DarkSurfaceVariant

private val ICON_SIZE = 72.dp
private val ICON_INNER_SIZE = 36.dp
private val NAME_HEIGHT = 40.dp
private val SLIDER_WIDTH = 100.dp
private val TILE_PADDING = 8.dp
private val SPACING_SMALL = 4.dp
private val SPACING_MEDIUM = 8.dp

@Composable
fun SoundTile(
    icon: ImageVector,
    name: String,
    isEnabled: Boolean,
    volume: Float,
    onToggle: () -> Unit,
    onVolumeChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(TILE_PADDING),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Circular icon area
        Box(
            modifier = Modifier
                .size(ICON_SIZE)
                .clip(CircleShape)
                .background(if (isEnabled) BlueAccent else DarkSurfaceVariant)
                .clickable(onClick = onToggle),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = name,
                modifier = Modifier.size(ICON_INNER_SIZE),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(SPACING_MEDIUM))

        // Sound name
        Text(
            text = name,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            maxLines = 2,
            modifier = Modifier.height(NAME_HEIGHT)
        )

        Spacer(modifier = Modifier.height(SPACING_SMALL))

        // Volume slider
        Slider(
            value = volume,
            onValueChange = onVolumeChange,
            modifier = Modifier.width(SLIDER_WIDTH),
            enabled = isEnabled,
            colors = SliderDefaults.colors(
                thumbColor = BlueAccent,
                activeTrackColor = BlueAccent,
                inactiveTrackColor = DarkSurfaceVariant
            )
        )
    }
}
