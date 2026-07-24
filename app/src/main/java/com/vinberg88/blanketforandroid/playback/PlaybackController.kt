package com.vinberg88.blanketforandroid.playback

/** Bridges notification and widget actions to the active playback view model. */
object PlaybackController {
    var onTogglePlayback: (() -> Unit)? = null
}
