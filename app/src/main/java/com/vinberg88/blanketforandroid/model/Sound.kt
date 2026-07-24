package com.vinberg88.blanketforandroid.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

data class Sound(
    val id: String,
    val fileName: String,
    val displayName: String,
    val icon: ImageVector,
    val iconName: String = "",
    val isCustom: Boolean = false
)

fun iconForName(iconName: String): ImageVector = when (iconName) {
    "audiotrack" -> Icons.Default.Audiotrack
    "graphic_eq" -> Icons.Default.GraphicEq
    "headphones" -> Icons.Default.Headphones
    "radio" -> Icons.Default.Radio
    "library_music" -> Icons.Default.LibraryMusic
    else -> Icons.Default.MusicNote
}

val availableSounds = listOf(
    Sound("rain", "rain.ogg", "Rain", Icons.Default.WaterDrop),
    Sound("storm", "storm.ogg", "Storm", Icons.Default.Thunderstorm),
    Sound("wind", "wind.ogg", "Wind", Icons.Default.Air),
    Sound("waves", "waves.ogg", "Waves", Icons.Default.Waves),
    Sound("stream", "stream.ogg", "Stream", Icons.Default.Water),
    Sound("birds", "birds.ogg", "Birds", Icons.Default.Pets),
    Sound("summer_night", "summer-night.ogg", "Summer Night", Icons.Default.NightsStay),
    Sound("train", "train.ogg", "Train", Icons.Default.Train),
    Sound("boat", "boat.ogg", "Boat", Icons.Default.DirectionsBoat),
    Sound("city", "city.ogg", "City", Icons.Default.LocationCity),
    Sound("coffee_shop", "coffee-shop.ogg", "Coffee Shop", Icons.Default.LocalCafe),
    Sound("fireplace", "fireplace.ogg", "Fire Place", Icons.Default.LocalFireDepartment),
    Sound("white_noise", "white-noise.ogg", "White Noise", Icons.Default.GraphicEq),
    Sound("pink_noise", "pink-noise.ogg", "Pink Noise", Icons.Default.Equalizer)
)
