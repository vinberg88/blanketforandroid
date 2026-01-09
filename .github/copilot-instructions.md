# Copilot instructions for this repo

## Project overview
- Android app (Kotlin) implementing **Blanket** (multi-track ambient audio mixer).
- UI is **Jetpack Compose + Material3**; state is **MVVM** with `BlanketViewModel`.
- Audio engine uses one `MediaPlayer` per sound (looping) and mixes concurrently.
- Persistence is **DataStore Preferences** (per-sound enabled + volume + global isPlaying).

## Where to look first
- Architecture writeups: `BUILD_NOTES.md`, `IMPLEMENTATION.md`
- App module Gradle: `app/build.gradle.kts`
- Compose entry point: `app/src/main/java/com/vinberg88/blanketforandroid/MainActivity.kt`

## Key codepaths and data flow
- Sound catalog: `model/availableSounds` (see `app/src/main/java/.../model/Sound.kt`)
- UI → ViewModel:
  - Main screen grid: `ui/screens/MainScreen.kt` renders `availableSounds` and binds to `BlanketViewModel.soundStates`.
  - Tile component: `ui/components/SoundTile.kt` calls `toggleSound()` and `setSoundVolume()`.
- ViewModel orchestration: `viewmodel/BlanketViewModel.kt`
  - Loads all sounds on init via `audioPlayer.loadSound(sound)`.
  - Restores persisted state using `combine(availableSounds.map { prefsRepository.getSoundState(it.id) })`.
  - Uses `AtomicBoolean hasAutoStarted` to ensure auto-restore runs once.
- Persistence keys (DataStore): `data/PreferencesRepository.kt`
  - `is_playing`, `sound_enabled_<id>`, `sound_volume_<id>`.
- Audio mixing: `audio/AudioPlayer.kt`
  - Assets are loaded from `assets/sounds/<fileName>`; each player is looping.

## Build & artifacts (repo-specific)
- Use Gradle wrapper from repo root.
- Stable artifact tasks (copy outputs into `dist/`):
  - `./gradlew :app:buildBlanketApk` → `dist/blanket.apk` (debug)
  - `./gradlew :app:buildBlanketInternalApk` → `dist/blanket-internal.apk` (debug-signed "internal" build)
  - `./gradlew :app:buildBlanketReleaseApk` → `dist/blanket-release.apk` (release; signing not configured by default)
- Signing checks:
  - `./gradlew :app:signingReport`
  - Compatibility alias: `./gradlew :app:validateReleaseSigning` (prefers release/internal/debug validation)

## Conventions/patterns to follow when changing code
- Prefer updating `BlanketViewModel` + DataStore flows for new state; UI reads state via `collectAsState()`.
- When adding a new sound, update `availableSounds` and ensure an asset exists under `app/src/main/assets/sounds/`.
- Keep volumes as `Float` in [0.0, 1.0] end-to-end (slider uses the same range).
