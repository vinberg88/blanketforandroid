package com.vinberg88.blanketforandroid

import com.vinberg88.blanketforandroid.model.SoundState
import com.vinberg88.blanketforandroid.model.availableSounds
import com.vinberg88.blanketforandroid.ui.screens.orderSoundsForDisplay
import com.vinberg88.blanketforandroid.viewmodel.savedMixVolumesForEnabledSounds
import org.junit.Assert.assertEquals
import org.junit.Test

class ReviewFixesTest {
    @Test
    fun favoriteSortingKeepsCatalogOrderWithinEachGroup() {
        val orderedIds = orderSoundsForDisplay(
            allSounds = availableSounds,
            favoriteSoundIds = setOf("waves", "city")
        ).map { it.id }

        assertEquals(
            listOf(
                "waves",
                "city",
                "rain",
                "storm",
                "wind",
                "stream",
                "birds",
                "summer_night",
                "train",
                "boat",
                "coffee_shop",
                "fireplace",
                "white_noise",
                "pink_noise"
            ),
            orderedIds
        )
    }

    @Test
    fun savedMixVolumesOnlyIncludeEnabledSounds() {
        val volumes = savedMixVolumesForEnabledSounds(
            mapOf(
                "rain" to SoundState("rain", isEnabled = true, volume = 0.32f),
                "storm" to SoundState("storm", isEnabled = false, volume = 0.52f),
                "waves" to SoundState("waves", isEnabled = true, volume = 0.55f)
            )
        )

        assertEquals(
            mapOf(
                "rain" to 0.32f,
                "waves" to 0.55f
            ),
            volumes
        )
    }
}
