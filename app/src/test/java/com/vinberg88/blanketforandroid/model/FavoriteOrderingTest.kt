package com.vinberg88.blanketforandroid.model

import org.junit.Assert.assertEquals
import org.junit.Test

class FavoriteOrderingTest {
    @Test
    fun favoriteFirstSortingKeepsCatalogRelativeOrder() {
        val favorites = setOf("waves", "rain")

        val ordered = availableSounds
            .sortedByDescending { it.id in favorites }
            .map { it.id }

        assertEquals(
            listOf(
                "rain",
                "waves",
                "storm",
                "wind",
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
            ordered
        )
    }
}
