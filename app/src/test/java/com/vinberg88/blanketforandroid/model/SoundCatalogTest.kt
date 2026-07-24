package com.vinberg88.blanketforandroid.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SoundCatalogTest {
    @Test
    fun catalogContainsTheLicensedBlanketSoundsInUiOrder() {
        assertEquals(
            listOf(
                "rain",
                "storm",
                "wind",
                "waves",
                "stream",
                "birds",
                "summer_night",
                "train",
                "boat",
                "city",
                "coffee_shop",
                "fireplace",
                "white_noise",
                "pink_noise"
            ),
            availableSounds.map { it.id }
        )
    }

    @Test
    fun catalogIdsAndAssetNamesAreUniqueOggFiles() {
        assertEquals(availableSounds.size, availableSounds.map { it.id }.toSet().size)
        assertEquals(availableSounds.size, availableSounds.map { it.fileName }.toSet().size)
        assertTrue(availableSounds.all { it.fileName.endsWith(".ogg") })
    }
}
