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
    Sound("rain", "Rain.wav", "Rain", Icons.Default.WaterDrop),
    Sound("storm", "Storm.wav", "Storm", Icons.Default.Thunderstorm),
    Sound("river", "River.wav", "Waves", Icons.Default.Waves),
    Sound("hello", "Hello.wav", "Hello", Icons.Default.RecordVoiceOver),
    Sound("birds", "Birds.wav", "Birds", Icons.Default.Pets),
    Sound("boat", "Boat.wav", "Boat", Icons.Default.DirectionsBoat),
    Sound("coffee_shop", "CoffeeShop.wav", "Coffee Shop", Icons.Default.LocalCafe),
    Sound("fireplace", "Fireplace.wav", "Fireplace", Icons.Default.LocalFireDepartment),
    Sound("metro", "Metro.wav", "Metro", Icons.Default.Train),
    Sound("nyc", "NYC.wav", "New York City", Icons.Default.LocationCity),
    Sound("night", "Night.wav", "Night", Icons.Default.NightsStay),
    Sound("pixel", "Pixel.wav", "Pixel", Icons.Default.Smartphone),
    Sound("robot", "Robot.wav", "Robot", Icons.Default.SmartToy),
    Sound("siren", "Siren.wav", "Siren", Icons.Default.Sos)
)
