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
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Circular icon area
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(if (isEnabled) BlueAccent else DarkSurfaceVariant)
                .clickable(onClick = onToggle),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = name,
                modifier = Modifier.size(36.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Sound name
        Text(
            text = name,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            maxLines = 2,
            modifier = Modifier.height(40.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Volume slider
        Slider(
            value = volume,
            onValueChange = onVolumeChange,
            modifier = Modifier.width(100.dp),
            enabled = isEnabled,
            colors = SliderDefaults.colors(
                thumbColor = BlueAccent,
                activeTrackColor = BlueAccent,
                inactiveTrackColor = DarkSurfaceVariant
            )
        )
    }
}
