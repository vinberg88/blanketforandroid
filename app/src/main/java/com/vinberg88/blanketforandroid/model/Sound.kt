package com.vinberg88.blanketforandroid.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

data class Sound(
    val id: String,
    val fileName: String,
    val displayName: String,
    val icon: ImageVector
)

val availableSounds = listOf(
    Sound("birds", "Birds.wav", "Birds", Icons.Default.FlutterDash),
    Sound("boat", "Boat.wav", "Boat", Icons.Default.DirectionsBoat),
    Sound("coffee_shop", "CoffeeShop.wav", "Coffee Shop", Icons.Default.Coffee),
    Sound("fireplace", "Fireplace.wav", "Fireplace", Icons.Default.LocalFireDepartment),
    Sound("hello", "Hello.wav", "Hello", Icons.Default.Waving),
    Sound("metro", "Metro.wav", "Metro", Icons.Default.Train),
    Sound("nyc", "NYC.wav", "New York City", Icons.Default.LocationCity),
    Sound("night", "Night.wav", "Night", Icons.Default.NightsStay),
    Sound("pixel", "Pixel.wav", "Pixel", Icons.Default.Smartphone),
    Sound("rain", "Rain.wav", "Rain", Icons.Default.WaterDrop),
    Sound("river", "River.wav", "River", Icons.Default.Waves),
    Sound("robot", "Robot.wav", "Robot", Icons.Default.SmartToy),
    Sound("siren", "Siren.wav", "Siren", Icons.Default.Sos),
    Sound("storm", "Storm.wav", "Storm", Icons.Default.Thunderstorm)
)
