package com.vinberg88.blanketforandroid.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vinberg88.blanketforandroid.ui.theme.BlueAccent
import com.vinberg88.blanketforandroid.ui.theme.DarkSurfaceVariant
import com.vinberg88.blanketforandroid.ui.theme.SelectedSoundBackground

private val ICON_SIZE = 68.dp
private val ICON_INNER_SIZE = 42.dp
private val NAME_HEIGHT = 40.dp
private val SLIDER_WIDTH = 78.dp

@Composable
fun SoundTile(
    soundId: String,
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
            .fillMaxWidth()
            .padding(horizontal = 3.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Box(
            modifier = Modifier
                .size(ICON_SIZE)
                .clip(CircleShape)
                .background(if (isEnabled) SelectedSoundBackground else Color.Transparent)
                .semantics { selected = isEnabled }
                .clickable(role = Role.Checkbox, onClick = onToggle),
            contentAlignment = Alignment.Center
        ) {
            BlanketSoundIcon(
                soundId = soundId,
                fallbackIcon = icon,
                contentDescription = if (isEnabled) "$name enabled" else "$name disabled",
                modifier = Modifier.size(ICON_INNER_SIZE),
                tint = if (isEnabled) BlueAccent else MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = name,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            maxLines = 2,
            modifier = Modifier.height(NAME_HEIGHT)
        )

        Slider(
            value = volume.coerceIn(0f, 1f),
            onValueChange = onVolumeChange,
            modifier = Modifier
                .width(SLIDER_WIDTH)
                .height(28.dp),
            colors = SliderDefaults.colors(
                thumbColor = if (isEnabled) BlueAccent else DarkSurfaceVariant,
                activeTrackColor = if (isEnabled) BlueAccent else DarkSurfaceVariant,
                inactiveTrackColor = DarkSurfaceVariant,
                disabledThumbColor = DarkSurfaceVariant,
                disabledActiveTrackColor = DarkSurfaceVariant,
                disabledInactiveTrackColor = DarkSurfaceVariant
            )
        )
    }
}

@Composable
private fun BlanketSoundIcon(
    soundId: String,
    fallbackIcon: ImageVector,
    contentDescription: String,
    modifier: Modifier,
    tint: Color
) {
    when (soundId) {
        "rain" -> RainIcon(modifier, tint)
        "storm" -> StormIcon(modifier, tint)
        "wind" -> WindIcon(modifier, tint)
        "waves" -> WavesIcon(modifier, tint)
        "stream" -> StreamIcon(modifier, tint)
        "summer_night" -> MoonIcon(modifier, tint)
        else -> Icon(
            imageVector = fallbackIcon,
            contentDescription = contentDescription,
            modifier = modifier,
            tint = tint
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.iconStroke() =
    Stroke(width = 2.8.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)

@Composable
private fun RainIcon(modifier: Modifier, tint: Color) = Canvas(modifier) {
    val stroke = iconStroke()
    val cloud = Path().apply {
        moveTo(size.width * .15f, size.height * .54f)
        cubicTo(size.width * .12f, size.height * .34f, size.width * .28f, size.height * .28f, size.width * .38f, size.height * .31f)
        cubicTo(size.width * .47f, size.height * .10f, size.width * .76f, size.height * .17f, size.width * .79f, size.height * .42f)
        cubicTo(size.width * .95f, size.height * .43f, size.width * .94f, size.height * .67f, size.width * .76f, size.height * .67f)
        lineTo(size.width * .19f, size.height * .67f)
    }
    drawPath(cloud, tint, style = stroke)
    listOf(.32f, .52f, .72f).forEach {
        drawLine(tint, Offset(size.width * it, size.height * .78f), Offset(size.width * (it - .05f), size.height * .95f), 2.5.dp.toPx(), StrokeCap.Round)
    }
}

@Composable
private fun StormIcon(modifier: Modifier, tint: Color) = Canvas(modifier) {
    val stroke = iconStroke()
    val cloud = Path().apply {
        moveTo(size.width * .15f, size.height * .49f)
        cubicTo(size.width * .12f, size.height * .30f, size.width * .29f, size.height * .25f, size.width * .38f, size.height * .29f)
        cubicTo(size.width * .48f, size.height * .08f, size.width * .77f, size.height * .15f, size.width * .79f, size.height * .39f)
        cubicTo(size.width * .95f, size.height * .41f, size.width * .94f, size.height * .62f, size.width * .76f, size.height * .62f)
        lineTo(size.width * .18f, size.height * .62f)
    }
    val bolt = Path().apply {
        moveTo(size.width * .54f, size.height * .64f)
        lineTo(size.width * .39f, size.height * .86f)
        lineTo(size.width * .55f, size.height * .86f)
        lineTo(size.width * .48f, size.height)
        lineTo(size.width * .71f, size.height * .75f)
        lineTo(size.width * .57f, size.height * .75f)
    }
    drawPath(cloud, tint, style = stroke)
    drawPath(bolt, tint, style = stroke)
}

@Composable
private fun WindIcon(modifier: Modifier, tint: Color) = Canvas(modifier) {
    val width = 2.7.dp.toPx()
    drawLine(tint, Offset(size.width * .08f, size.height * .35f), Offset(size.width * .70f, size.height * .35f), width, StrokeCap.Round)
    drawArc(tint, -90f, 230f, false, Offset(size.width * .57f, size.height * .12f), androidx.compose.ui.geometry.Size(size.width * .34f, size.height * .36f), style = Stroke(width, cap = StrokeCap.Round))
    drawLine(tint, Offset(size.width * .08f, size.height * .55f), Offset(size.width * .82f, size.height * .55f), width, StrokeCap.Round)
    drawArc(tint, -80f, 245f, false, Offset(size.width * .64f, size.height * .37f), androidx.compose.ui.geometry.Size(size.width * .30f, size.height * .40f), style = Stroke(width, cap = StrokeCap.Round))
    drawLine(tint, Offset(size.width * .08f, size.height * .75f), Offset(size.width * .58f, size.height * .75f), width, StrokeCap.Round)
}

@Composable
private fun WavesIcon(modifier: Modifier, tint: Color) = Canvas(modifier) {
    val stroke = iconStroke()
    val wave = Path().apply {
        moveTo(size.width * .23f, size.height * .88f)
        lineTo(size.width * .23f, size.height * .31f)
        cubicTo(size.width * .23f, size.height * .10f, size.width * .56f, size.height * .10f, size.width * .56f, size.height * .34f)
        lineTo(size.width * .56f, size.height * .58f)
        cubicTo(size.width * .56f, size.height * .77f, size.width * .74f, size.height * .88f, size.width * .91f, size.height * .88f)
    }
    val inner = Path().apply {
        moveTo(size.width * .42f, size.height * .88f)
        lineTo(size.width * .42f, size.height * .35f)
        cubicTo(size.width * .42f, size.height * .25f, size.width * .54f, size.height * .25f, size.width * .54f, size.height * .37f)
    }
    drawPath(wave, tint, style = stroke)
    drawPath(inner, tint, style = stroke)
}

@Composable
private fun StreamIcon(modifier: Modifier, tint: Color) = Canvas(modifier) {
    val strokeWidth = 2.6.dp.toPx()
    val boat = Path().apply {
        moveTo(size.width * .18f, size.height * .55f)
        lineTo(size.width * .38f, size.height * .30f)
        lineTo(size.width * .45f, size.height * .60f)
        lineTo(size.width * .78f, size.height * .60f)
        cubicTo(size.width * .70f, size.height * .80f, size.width * .37f, size.height * .83f, size.width * .24f, size.height * .66f)
    }
    drawPath(boat, tint, style = Stroke(strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
    listOf(.76f, .91f).forEach { y ->
        val water = Path().apply {
            moveTo(size.width * .08f, size.height * y)
            cubicTo(size.width * .18f, size.height * (y - .08f), size.width * .27f, size.height * (y + .08f), size.width * .37f, size.height * y)
            cubicTo(size.width * .47f, size.height * (y - .08f), size.width * .57f, size.height * (y + .08f), size.width * .67f, size.height * y)
            cubicTo(size.width * .77f, size.height * (y - .08f), size.width * .85f, size.height * (y + .06f), size.width * .93f, size.height * y)
        }
        drawPath(water, tint, style = Stroke(strokeWidth, cap = StrokeCap.Round))
    }
}

@Composable
private fun MoonIcon(modifier: Modifier, tint: Color) = Canvas(modifier) {
    val moon = Path().apply {
        moveTo(size.width * .64f, size.height * .10f)
        cubicTo(size.width * .31f, size.height * .22f, size.width * .28f, size.height * .72f, size.width * .61f, size.height * .89f)
        cubicTo(size.width * .78f, size.height * .98f, size.width * .93f, size.height * .88f, size.width * .97f, size.height * .80f)
        cubicTo(size.width * .65f, size.height * .84f, size.width * .47f, size.height * .38f, size.width * .64f, size.height * .10f)
    }
    drawPath(moon, tint, style = iconStroke())
    drawCircle(tint, 2.5.dp.toPx(), Offset(size.width * .83f, size.height * .22f))
}
