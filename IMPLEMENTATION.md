# Blanket for Android - Implementation Details

## Overview

This document describes the complete implementation of the Blanket audio mixer for Android, inspired by the Linux desktop application. The app provides a dark-themed UI for mixing multiple ambient sounds with individual volume controls and state persistence.

## Architecture

The app follows the **MVVM (Model-View-ViewModel)** architecture pattern with Jetpack Compose:

```
┌─────────────────┐
│   MainActivity  │  (Compose Entry Point)
└────────┬────────┘
         │
    ┌────▼──────────────┐
    │  MainScreen (UI)  │
    └────────┬──────────┘
             │
    ┌────────▼─────────────┐
    │  BlanketViewModel    │  (State Management)
    └────┬────────┬────────┘
         │        │
    ┌────▼──┐  ┌─▼───────────────┐
    │ Audio │  │ Preferences     │
    │Player │  │ Repository      │
    └───────┘  └─────────────────┘
```

## Components

### 1. Data Models (`model/`)

#### `Sound.kt`
Defines the sound data structure and provides the list of all available sounds:

```kotlin
data class Sound(
    val id: String,           // Unique identifier
    val fileName: String,     // Asset filename
    val displayName: String,  // UI display name
    val icon: ImageVector     // Material icon
)
```

14 sounds are pre-configured with appropriate icons and display names:
- Birds, Boat, Coffee Shop, Fireplace, Hello, Metro, NYC, Night, Pixel, Rain, River, Robot, Siren, Storm

#### `SoundState.kt`
Represents the state of each sound:

```kotlin
data class SoundState(
    val soundId: String,
    val isEnabled: Boolean,   // Is this sound active?
    val volume: Float         // Volume level (0.0 - 1.0)
)

data class AppState(
    val isPlaying: Boolean,           // Global play/pause state
    val soundStates: Map<String, SoundState>
)
```

### 2. Audio Engine (`audio/`)

#### `AudioPlayer.kt`
Manages multi-track audio playback using Android's MediaPlayer:

**Key Features:**
- Maintains a `Map<String, MediaPlayer>` for concurrent playback
- Loads sounds asynchronously from assets using coroutines
- Configures MediaPlayer with looping enabled
- Individual volume control per sound (0.0 - 1.0)
- Global pause/resume for all active tracks

**Main Methods:**
- `loadSound(sound: Sound)`: Loads a sound from assets and prepares MediaPlayer
- `play(soundId: String)`: Starts playback for a specific sound
- `pause(soundId: String)`: Pauses a specific sound
- `setVolume(soundId: String, volume: Float)`: Adjusts volume
- `pauseAll()`: Pauses all playing sounds
- `resumeAll(enabledSounds: Set<String>)`: Resumes specified sounds
- `release()`: Cleans up all MediaPlayer resources

### 3. Persistence Layer (`data/`)

#### `PreferencesRepository.kt`
Handles state persistence using DataStore Preferences:

**Stored Data:**
- Global play/pause state: `is_playing`
- Per-sound enabled state: `sound_enabled_{soundId}`
- Per-sound volume: `sound_volume_{soundId}`

**Key Methods:**
- `isPlaying: Flow<Boolean>`: Observable play state
- `getSoundState(soundId: String): Flow<SoundState>`: Observable sound state
- `setIsPlaying(isPlaying: Boolean)`: Update global play state
- `setSoundEnabled(soundId: String, enabled: Boolean)`: Update sound enabled state
- `setSoundVolume(soundId: String, volume: Float)`: Update sound volume

### 4. ViewModel (`viewmodel/`)

#### `BlanketViewModel.kt`
Central state management and business logic:

**Initialization:**
1. Loads all 14 sounds into AudioPlayer asynchronously
2. Observes DataStore for persisted play state
3. Combines all sound state flows using `combine()`
4. Auto-restores playback if app was playing on last close

**State Flows:**
- `soundStates: StateFlow<Map<String, SoundState>>`: Current state of all sounds
- `isPlaying: StateFlow<Boolean>`: Current play/pause state

**User Actions:**
- `toggleSound(soundId: String)`: Enable/disable a sound and update playback
- `setSoundVolume(soundId: String, volume: Float)`: Adjust volume and persist
- `togglePlayPause()`: Start/stop all enabled sounds

**Lifecycle:**
- Properly releases AudioPlayer resources in `onCleared()`

### 5. UI Theme (`ui/theme/`)

#### Dark Theme Colors
Matching the Blanket Linux aesthetic:
- **Background**: `#1E1E1E` (dark gray)
- **Surface**: `#2D2D2D` (slightly lighter)
- **Surface Variant**: `#3A3A3A` (for inactive elements)
- **Blue Accent**: `#4A90E2` (primary action color)
- **Light Text**: `#E0E0E0` (high contrast)

#### Typography
Uses Material3 default typography with adjustments for readability on dark backgrounds.

### 6. UI Components (`ui/components/`)

#### `SoundTile.kt`
Reusable sound tile component:

**Structure:**
```
┌─────────────────┐
│   ┌─────────┐   │
│   │  Icon   │   │  ← Circular, colored when active
│   └─────────┘   │
│   Sound Name    │  ← Display name
│   ━━━━━●━━━━━   │  ← Volume slider
└─────────────────┘
```

**Features:**
- 72dp circular icon area with background color indicating active state
- Icon color changes based on enabled/disabled
- Text label with max 2 lines
- Slider with blue accent when active, disabled visual when sound is off
- Click on icon to toggle sound
- Drag slider to adjust volume

### 7. Main Screen (`ui/screens/`)

#### `MainScreen.kt`
The main app screen with full layout:

**Layout Structure:**
```
┌──────────────────────┐
│  Top Bar: "Blanket"  │
├──────────────────────┤
│  ┌────┐  ┌────┐     │
│  │Tile│  │Tile│     │  ← 2-column grid
│  └────┘  └────┘     │
│  ┌────┐  ┌────┐     │
│  │Tile│  │Tile│     │
│  └────┘  └────┘     │
│        ...           │
├──────────────────────┤
│      ┌─────┐        │  ← Centered Play/Pause
│      │  ▶  │        │
│      └─────┘        │
└──────────────────────┘
```

**Features:**
- Top app bar with "Blanket" title
- `LazyVerticalGrid` with 2 columns for efficient rendering
- Bottom bar with centered 56dp icon button
- Play/Pause icon changes dynamically
- Proper padding and spacing throughout

### 8. Main Activity (`MainActivity.kt`)

Minimal Compose setup:
- Extends `ComponentActivity` (Compose-friendly)
- Initializes `BlanketViewModel` using `viewModels()` delegate
- Sets content with `BlanketForAndroidTheme`
- Renders `MainScreen` with ViewModel

## Data Flow

### User Toggles a Sound
```
User taps tile
  → SoundTile onClick
    → viewModel.toggleSound(soundId)
      → Update DataStore (setSoundEnabled)
        → DataStore emits new state
          → ViewModel observes change
            → Updates soundStates StateFlow
              → Compose recomposes SoundTile
      → If playing: AudioPlayer.play() or pause()
```

### User Adjusts Volume
```
User drags slider
  → SoundTile onVolumeChange
    → viewModel.setSoundVolume(soundId, volume)
      → Update DataStore (setSoundVolume)
        → DataStore emits new state
          → ViewModel observes change
            → Updates soundStates StateFlow
              → Compose recomposes SoundTile
      → AudioPlayer.setVolume(soundId, volume)
```

### User Toggles Play/Pause
```
User taps Play/Pause
  → MainScreen button onClick
    → viewModel.togglePlayPause()
      → Update DataStore (setIsPlaying)
        → DataStore emits new state
          → ViewModel observes change
            → Updates isPlaying StateFlow
              → Compose recomposes button icon
      → If playing: AudioPlayer.resumeAll(enabledSounds)
      → If pausing: AudioPlayer.pauseAll()
```

### App Launch Auto-Restore
```
App launches
  → BlanketViewModel.init()
    → Load all sounds into AudioPlayer
    → Observe DataStore isPlaying
    → Observe all sound states with combine()
      → First emission with hasAutoStarted=false
        → If isPlaying==true
          → For each enabled sound
            → AudioPlayer.setVolume(soundId, volume)
            → AudioPlayer.play(soundId)
          → Set hasAutoStarted=true
```

## File Organization

```
app/src/main/
├── assets/
│   └── sounds/              # 14 WAV files
├── java/com/vinberg88/blanketforandroid/
│   ├── MainActivity.kt
│   ├── audio/
│   │   └── AudioPlayer.kt
│   ├── data/
│   │   └── PreferencesRepository.kt
│   ├── model/
│   │   ├── Sound.kt
│   │   └── SoundState.kt
│   ├── viewmodel/
│   │   └── BlanketViewModel.kt
│   └── ui/
│       ├── components/
│       │   └── SoundTile.kt
│       ├── screens/
│       │   └── MainScreen.kt
│       └── theme/
│           ├── Color.kt
│           ├── Theme.kt
│           └── Type.kt
└── res/
    └── values/
        ├── strings.xml
        ├── colors.xml
        └── themes.xml
```

## Key Design Decisions

### 1. MediaPlayer vs ExoPlayer
**Choice**: MediaPlayer
**Reason**: Simpler API for basic looping audio, no external dependencies needed, sufficient for the use case.

### 2. DataStore vs SharedPreferences
**Choice**: DataStore
**Reason**: Modern, coroutine-based, type-safe, better error handling, recommended by Google.

### 3. Compose vs XML Views
**Choice**: Compose
**Reason**: Modern declarative UI, easier state management, better integration with ViewModels, less boilerplate.

### 4. StateFlow vs LiveData
**Choice**: StateFlow
**Reason**: Better Compose integration, more Kotlin-idiomatic, simpler API for state management.

### 5. combine() for Multiple Flows
**Choice**: `combine()` operator
**Reason**: Efficiently combines 14 sound state flows into a single Map emission, avoiding nested collect calls.

## Testing Recommendations

### Unit Tests
- `BlanketViewModel`: Test toggleSound, setSoundVolume, togglePlayPause logic
- `PreferencesRepository`: Test data persistence and retrieval
- `AudioPlayer`: Mock MediaPlayer to test state management

### Integration Tests
- Test full user flow: enable sound → adjust volume → play → pause → restart app
- Verify state persistence across app restarts
- Test multi-track concurrent playback

### UI Tests
- Verify grid layout renders 14 tiles correctly
- Test touch interactions on tiles and sliders
- Verify play/pause button state changes

## Performance Considerations

1. **Lazy Loading**: LazyVerticalGrid only renders visible tiles
2. **Async Sound Loading**: All sounds loaded in parallel with coroutines
3. **Efficient State Updates**: Only changed tiles recompose
4. **Resource Cleanup**: MediaPlayer instances properly released in onCleared()
5. **DataStore Efficiency**: Writes are batched and asynchronous

## Accessibility

The app includes basic accessibility support:
- Content descriptions on all interactive elements
- Material3 components with built-in accessibility
- High contrast dark theme for readability
- Slider components support TalkBack

## Known Limitations

1. **No Background Service**: Audio stops when app is killed (not just paused)
2. **No Audio Focus**: Doesn't handle phone calls or other audio interruptions
3. **No Sleep Timer**: Can't auto-stop after a duration
4. **Fixed Sound Set**: Can't import custom sounds
5. **No Presets**: Can't save/load named mixes

These are intentional v1 limitations as per requirements and can be addressed in future versions.
