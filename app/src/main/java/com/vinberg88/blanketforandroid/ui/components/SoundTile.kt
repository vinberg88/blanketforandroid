package com.vinberg88.blanketforandroid.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.drawscope.Stroke
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
private val DELETE_BUTTON_SIZE = 20.dp

@Composable
fun SoundTile(
    soundId: String,
    icon: ImageVector,
    name: String,
    isEnabled: Boolean,
    volume: Float,
    onToggle: () -> Unit,
    onVolumeChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    onDelete: (() -> Unit)? = null,
    onEdit: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .padding(TILE_PADDING),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Circular icon area with optional delete button overlay
        Box(
            contentAlignment = Alignment.TopEnd
        ) {
            Box(
                modifier = Modifier
                    .size(ICON_SIZE)
                    .clip(CircleShape)
                    .background(if (isEnabled) BlueAccent else DarkSurfaceVariant)
                    .clickable(onClick = onToggle),
                contentAlignment = Alignment.Center
            ) {
                BlanketLineIcon(
                    soundId = soundId,
                    fallbackIcon = icon,
                    contentDescription = name,
                    modifier = Modifier.size(ICON_INNER_SIZE),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            if (onEdit != null) {
                Box(
                    modifier = Modifier
                        .size(DELETE_BUTTON_SIZE)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable(onClick = onEdit),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit $name",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            } else if (onDelete != null) {
                Box(
                    modifier = Modifier
                        .size(DELETE_BUTTON_SIZE)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.error)
                        .clickable(onClick = onDelete),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove $name",
                        tint = MaterialTheme.colorScheme.onError,
                        modifier = Modifier.size(DELETE_BUTTON_SIZE)
                    )
                }
            }
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

@Composable
private fun BlanketLineIcon(
    soundId: String,
    fallbackIcon: ImageVector,
    contentDescription: String,
    modifier: Modifier,
    tint: androidx.compose.ui.graphics.Color
) {
    when (soundId) {
        "rain" -> RainLineIcon(modifier, tint)
        "storm" -> StormLineIcon(modifier, tint)
        "river" -> WavesLineIcon(modifier, tint)
        else -> Icon(
            imageVector = fallbackIcon,
            contentDescription = contentDescription,
            modifier = modifier,
            tint = tint
        )
    }
}

@Composable
private fun RainLineIcon(modifier: Modifier, tint: androidx.compose.ui.graphics.Color) {
    Canvas(modifier = modifier) {
        val stroke = Stroke(width = 3.2f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        val cloud = Path().apply {
            moveTo(size.width * 0.15f, size.height * 0.52f)
            cubicTo(size.width * 0.12f, size.height * 0.33f, size.width * 0.31f, size.height * 0.31f, size.width * 0.35f, size.height * 0.31f)
            cubicTo(size.width * 0.43f, size.height * 0.12f, size.width * 0.76f, size.height * 0.18f, size.width * 0.78f, size.height * 0.43f)
            cubicTo(size.width * 0.95f, size.height * 0.45f, size.width * 0.93f, size.height * 0.67f, size.width * 0.76f, size.height * 0.67f)
            lineTo(size.width * 0.18f, size.height * 0.67f)
        }
        drawPath(cloud, tint, style = stroke)
        drawLine(tint, Offset(size.width * 0.30f, size.height * 0.78f), Offset(size.width * 0.24f, size.height * 0.96f), strokeWidth = 3f, cap = StrokeCap.Round)
        drawLine(tint, Offset(size.width * 0.50f, size.height * 0.78f), Offset(size.width * 0.44f, size.height * 0.96f), strokeWidth = 3f, cap = StrokeCap.Round)
        drawLine(tint, Offset(size.width * 0.70f, size.height * 0.78f), Offset(size.width * 0.64f, size.height * 0.96f), strokeWidth = 3f, cap = StrokeCap.Round)
    }
}

@Composable
private fun StormLineIcon(modifier: Modifier, tint: androidx.compose.ui.graphics.Color) {
    Canvas(modifier = modifier) {
        val stroke = Stroke(width = 3.2f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        val cloud = Path().apply {
            moveTo(size.width * 0.15f, size.height * 0.48f)
            cubicTo(size.width * 0.12f, size.height * 0.30f, size.width * 0.31f, size.height * 0.28f, size.width * 0.35f, size.height * 0.28f)
            cubicTo(size.width * 0.43f, size.height * 0.10f, size.width * 0.76f, size.height * 0.16f, size.width * 0.78f, size.height * 0.39f)
            cubicTo(size.width * 0.95f, size.height * 0.42f, size.width * 0.93f, size.height * 0.62f, size.width * 0.76f, size.height * 0.62f)
            lineTo(size.width * 0.18f, size.height * 0.62f)
        }
        val bolt = Path().apply {
            moveTo(size.width * 0.52f, size.height * 0.64f)
            lineTo(size.width * 0.38f, size.height * 0.88f)
            lineTo(size.width * 0.55f, size.height * 0.88f)
            lineTo(size.width * 0.45f, size.height)
            lineTo(size.width * 0.72f, size.height * 0.75f)
            lineTo(size.width * 0.56f, size.height * 0.75f)
        }
        drawPath(cloud, tint, style = stroke)
        drawPath(bolt, tint, style = stroke)
    }
}

@Composable
private fun WavesLineIcon(modifier: Modifier, tint: androidx.compose.ui.graphics.Color) {
    Canvas(modifier = modifier) {
        val stroke = Stroke(width = 3.4f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        val wave = Path().apply {
            moveTo(size.width * 0.22f, size.height * 0.88f)
            lineTo(size.width * 0.22f, size.height * 0.32f)
            cubicTo(size.width * 0.22f, size.height * 0.10f, size.width * 0.55f, size.height * 0.10f, size.width * 0.55f, size.height * 0.34f)
            lineTo(size.width * 0.55f, size.height * 0.58f)
            cubicTo(size.width * 0.55f, size.height * 0.76f, size.width * 0.73f, size.height * 0.88f, size.width * 0.90f, size.height * 0.88f)
        }
        val inner = Path().apply {
            moveTo(size.width * 0.42f, size.height * 0.88f)
            lineTo(size.width * 0.42f, size.height * 0.34f)
            cubicTo(size.width * 0.42f, size.height * 0.26f, size.width * 0.52f, size.height * 0.26f, size.width * 0.52f, size.height * 0.36f)
        }
        drawPath(wave, tint, style = stroke)
        drawPath(inner, tint, style = stroke)
    }
}
