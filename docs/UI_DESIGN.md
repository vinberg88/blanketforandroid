# Blanket UI Design - Visual Reference

## Color Palette

```
Dark Background:    #1E1E1E  ███████
Dark Surface:       #2D2D2D  ███████
Surface Variant:    #3A3A3A  ███████
Blue Accent:        #4A90E2  ███████
Light Text:         #E0E0E0  ███████
```

## Layout Structure

### Main Screen (Portrait)

```
┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
┃  Blanket                        ┃ ← Top Bar (#2D2D2D)
┣━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┫
┃                                  ┃
┃   ┌─────────┐    ┌─────────┐   ┃
┃   │    🐦   │    │    ⛵   │   ┃ ← Sound Tiles
┃   │  Birds  │    │  Boat   │   ┃   (2 columns)
┃   │ ━━━●━━━ │    │ ━━━━━━━ │   ┃
┃   └─────────┘    └─────────┘   ┃
┃                                  ┃
┃   ┌─────────┐    ┌─────────┐   ┃
┃   │    ☕   │    │    🔥   │   ┃
┃   │  Coffee │    │Fireplace│   ┃
┃   │   Shop  │    │ ━━━━●━━ │   ┃
┃   │ ━━━━━━━ │    └─────────┘   ┃
┃   └─────────┘                   ┃
┃                                  ┃
┃   ┌─────────┐    ┌─────────┐   ┃
┃   │    👋   │    │    🚇   │   ┃
┃   │  Hello  │    │  Metro  │   ┃
┃   │ ━━━━━━━ │    │ ━━━━━━━ │   ┃
┃   └─────────┘    └─────────┘   ┃
┃                                  ┃
┃   ┌─────────┐    ┌─────────┐   ┃
┃   │    🏙️   │    │    🌙   │   ┃
┃   │   NYC   │    │  Night  │   ┃
┃   │ ━━━━━━━ │    │ ━━━●━━━ │   ┃
┃   └─────────┘    └─────────┘   ┃
┃                                  ┃
┃         (scroll for more)        ┃
┃                                  ┃
┣━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┫
┃                                  ┃ ← Bottom Bar
┃           ┌─────┐               ┃   (#2D2D2D)
┃           │  ▶  │               ┃   Centered
┃           └─────┘               ┃   Play Button
┃                                  ┃
┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛
```

## Sound Tile States

### Inactive Tile (Sound Disabled)
```
┌───────────────┐
│  ┌─────────┐  │
│  │         │  │ ← Circle: #3A3A3A (dark gray)
│  │    🐦   │  │ ← Icon: #E0E0E0 (light)
│  │         │  │
│  └─────────┘  │
│               │
│     Birds     │ ← Text: #E0E0E0 (light)
│               │
│   ━━━●━━━━━  │ ← Slider: disabled appearance
│               │
└───────────────┘
```

### Active Tile (Sound Enabled & Playing)
```
┌───────────────┐
│  ┌─────────┐  │
│  │         │  │ ← Circle: #4A90E2 (blue)
│  │    🐦   │  │ ← Icon: #E0E0E0 (light)
│  │         │  │
│  └─────────┘  │
│               │
│     Birds     │ ← Text: #E0E0E0 (light)
│               │
│   ━━━━●━━━━  │ ← Slider: blue active track
│               │   Thumb position shows volume
└───────────────┘
```

## Interaction Behaviors

### Tile Tap (Toggle)
```
Tap on icon area → Toggle enabled/disabled
                 → Update circle color
                 → If playing: start/stop sound
                 → Save state to DataStore
```

### Slider Drag (Volume)
```
Drag slider → Update volume (0.0 - 1.0)
            → Apply to MediaPlayer
            → Save to DataStore
            → Only active if sound enabled
```

### Play/Pause Button
```
State: Stopped                State: Playing
┌─────────┐                  ┌─────────┐
│         │                  │         │
│    ▶    │  ───Tap───>      │   ⏸    │
│         │                  │         │
└─────────┘                  └─────────┘

Tap when stopped → Start all enabled sounds
                 → Change icon to Pause
                 → Save playing=true

Tap when playing → Pause all sounds
                 → Change icon to Play
                 → Save playing=false
```

## Typography

```
App Title:        22sp, Medium weight
Sound Names:      14sp, Medium weight
All text color:   #E0E0E0 (Light)
```

## Spacing & Sizing

```
Top Bar Height:       64dp
Bottom Bar Height:    80dp
Sound Tile:           ~160dp tall
  - Icon Circle:      72dp diameter
  - Icon:             36dp
  - Name Height:      40dp (max 2 lines)
  - Slider Width:     100dp
Tile Padding:         8dp
Grid Columns:         2
Play/Pause Button:    56dp
```

## Component Hierarchy

```
MainActivity
└── BlanketForAndroidTheme (Dark)
    └── Surface
        └── MainScreen
            ├── TopAppBar
            │   └── Text("Blanket")
            ├── LazyVerticalGrid (2 columns)
            │   └── items(availableSounds)
            │       └── SoundTile
            │           ├── Box (Circular icon area)
            │           │   └── Icon
            │           ├── Text (Sound name)
            │           └── Slider (Volume)
            └── BottomAppBar
                └── Row (centered)
                    └── FilledIconButton
                        └── Icon (Play/Pause)
```

## Dark Theme Benefits

1. **Reduced Eye Strain**: Dark background easier on eyes in low light
2. **Battery Savings**: OLED displays save power with dark pixels
3. **Content Focus**: Blue accents pop against dark background
4. **Modern Aesthetic**: Matches Blanket Linux design language
5. **Ambient Use**: Perfect for relaxation/sleep scenarios

## Material Design Compliance

- Follows Material3 Design guidelines
- Proper elevation and surface hierarchy
- Accessible contrast ratios (4.5:1+)
- Standard touch target sizes (48dp+)
- Consistent spacing and padding
- Material ripple effects on touch

## Responsive Behavior

- Grid automatically adjusts to screen width
- Scrollable content for all screen sizes
- Bottom bar always visible
- Safe area insets respected
- Works in portrait and landscape
