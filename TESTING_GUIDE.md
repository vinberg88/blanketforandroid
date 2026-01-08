# Testing Guide for Blanket for Android

## Prerequisites

- Android Studio (latest stable version recommended)
- Android device or emulator with API 21+ (Android 5.0+)
- Working internet connection for Gradle sync

## Build Instructions

### Step 1: Open in Android Studio

1. Launch Android Studio
2. Select "Open an Existing Project"
3. Navigate to the project directory
4. Select the root folder (containing `build.gradle.kts`)
5. Click "OK"

### Step 2: Gradle Sync

1. Android Studio will automatically start syncing Gradle
2. Wait for the sync to complete (may take a few minutes on first run)
3. If prompted, accept any SDK updates or plugin updates

### Step 3: Build the Project

#### Option A: Using Android Studio UI
1. From menu: Build → Make Project (Ctrl+F9 / Cmd+F9)
2. Wait for build to complete
3. Check Build output for any errors

#### Option B: Using Command Line
```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease
```

### Step 4: Run on Device/Emulator

1. Connect an Android device via USB (with USB debugging enabled)
   OR
   Start an Android emulator from AVD Manager

2. Select your device from the device dropdown in Android Studio

3. Click the Run button (green triangle) or press Shift+F10

4. The app should install and launch automatically

## Testing Checklist

### Visual Tests

- [ ] **App launches successfully**
  - No crashes on startup
  - Dark theme is applied
  - UI renders correctly

- [ ] **Top bar displays correctly**
  - Shows "Blanket" title
  - Background color is dark gray (#2D2D2D)
  - Text is light colored (#E0E0E0)

- [ ] **Sound grid renders**
  - 2 columns visible
  - 14 sound tiles present
  - Tiles are scrollable
  - All sound names are visible

- [ ] **Sound tiles look correct**
  - Circular icon area visible
  - Icons display (Material icons)
  - Sound names legible (max 2 lines)
  - Sliders present under each tile

- [ ] **Bottom bar displays correctly**
  - Bar is visible at bottom
  - Play button is centered
  - Play icon (▶) is visible

### Functional Tests

#### Audio Playback

- [ ] **Single sound playback**
  1. Tap on a sound tile (e.g., "Rain")
  2. Circle should turn blue
  3. Tap the Play button at bottom
  4. Sound should start playing and loop

- [ ] **Multi-track playback**
  1. Enable multiple sounds (e.g., "Rain", "Birds", "River")
  2. All circles should turn blue
  3. Tap Play button
  4. All enabled sounds should play simultaneously
  5. Sounds should blend together

- [ ] **Volume control**
  1. Enable a sound and start playback
  2. Adjust the slider left (lower volume)
  3. Sound should get quieter
  4. Adjust slider right (higher volume)
  5. Sound should get louder

- [ ] **Toggle on/off during playback**
  1. Start playback with a sound enabled
  2. Tap the sound tile to disable it
  3. Sound should stop immediately
  4. Tap again to re-enable
  5. Sound should start playing again

- [ ] **Play/Pause control**
  1. Enable some sounds
  2. Tap Play button (should change to Pause icon ⏸)
  3. Sounds should start playing
  4. Tap Pause button
  5. All sounds should pause
  6. Tap Play again
  7. Previously enabled sounds should resume

#### State Persistence

- [ ] **Save and restore sound states**
  1. Enable 3-4 sounds
  2. Set different volumes for each
  3. Close the app (swipe away from recent apps)
  4. Reopen the app
  5. Enabled sounds should still be enabled
  6. Volume sliders should be at saved positions

- [ ] **Save and restore playback state**
  1. Enable some sounds and tap Play
  2. Close the app while playing
  3. Reopen the app
  4. Sounds should automatically start playing
  5. Play/Pause button should show Pause icon

- [ ] **Persistence after device restart**
  1. Set up a mix with multiple sounds and volumes
  2. Start playback
  3. Restart the Android device
  4. Open the app
  5. Previous mix should be restored and auto-play

### Edge Cases

- [ ] **No sounds enabled**
  1. Disable all sounds
  2. Tap Play button
  3. No sound should play (expected behavior)
  4. No crash should occur

- [ ] **All sounds enabled**
  1. Enable all 14 sounds
  2. Tap Play
  3. All sounds should play simultaneously
  4. No performance issues or audio glitches

- [ ] **Rapid toggling**
  1. Rapidly tap a sound tile on/off multiple times
  2. App should remain responsive
  3. No crashes or audio artifacts

- [ ] **Volume at extremes**
  1. Set slider to minimum (0%)
  2. Sound should be silent or very quiet
  3. Set slider to maximum (100%)
  4. Sound should be at full volume

### Performance Tests

- [ ] **Smooth scrolling**
  1. Scroll through the sound grid
  2. Scrolling should be smooth (no lag or stuttering)

- [ ] **Responsive UI**
  1. Tap tiles and buttons
  2. UI should respond immediately
  3. No noticeable delay

- [ ] **Battery usage**
  1. Play multiple sounds for 5-10 minutes
  2. Check battery drain in device settings
  3. Should be reasonable for active audio playback

### Accessibility Tests

- [ ] **TalkBack support**
  1. Enable TalkBack (Settings → Accessibility)
  2. Navigate through the app
  3. All elements should have spoken descriptions
  4. Interactive elements should be focusable

- [ ] **Large text support**
  1. Increase system font size (Settings → Display → Font Size)
  2. Relaunch app
  3. Text should scale appropriately
  4. UI should remain usable

### Error Handling

- [ ] **Missing sound files**
  - All 14 sound files are bundled in assets
  - If any are missing, check app logs for errors

- [ ] **Low memory scenario**
  1. Open many other apps to consume memory
  2. Open Blanket app
  3. App should handle low memory gracefully
  4. May pause audio if system requests

## Known Issues & Limitations

1. **No background service**: Audio stops when app is killed (not when paused)
   - This is intentional for v1
   - Audio continues when screen is off or app is minimized
   - But stops if app is swiped away from recent apps

2. **No audio focus handling**: Doesn't pause for phone calls automatically
   - This is a v1 limitation
   - Manually pause before taking calls

3. **No fade in/out**: Audio starts/stops abruptly
   - Feature could be added in future version

## Troubleshooting

### Build Fails

**Issue**: Gradle sync fails with dependency errors
**Solution**: 
- Ensure Android Studio is up to date
- Check internet connection
- Try: Build → Clean Project, then Build → Rebuild Project
- Invalidate caches: File → Invalidate Caches / Restart

### App Crashes on Launch

**Issue**: App crashes immediately on startup
**Solution**:
- Check logcat for error messages
- Verify minimum API level (API 21+)
- Ensure device has sufficient storage

### No Audio Plays

**Issue**: Sounds don't play when enabled
**Solution**:
- Check device volume level
- Ensure device is not in silent mode
- Check logcat for MediaPlayer errors
- Verify sound files exist in assets/sounds/

### UI Looks Wrong

**Issue**: Dark theme not applied or colors incorrect
**Solution**:
- Verify app is using BlanketForAndroidTheme
- Check that Compose is properly configured
- Try uninstalling and reinstalling the app

## Logging

To view detailed logs:

1. Open Android Studio
2. Click "Logcat" tab at bottom
3. Select your device
4. Filter by package name: `com.vinberg88.blanketforandroid`
5. Look for errors or warnings

## Success Criteria

The app passes testing if:

✅ Builds without errors in Android Studio
✅ Launches successfully on API 21+ devices
✅ Dark theme is properly applied throughout
✅ All 14 sounds are playable
✅ Multiple sounds can play simultaneously
✅ Volume controls work for each sound
✅ Play/Pause controls all enabled sounds
✅ State persists across app restarts
✅ Auto-restore works when reopening app
✅ UI is responsive and smooth
✅ No crashes or major bugs

## Reporting Issues

If you encounter any issues:

1. Check logcat for error messages
2. Note the exact steps to reproduce
3. Record device model and Android version
4. Take screenshots if UI-related
5. Create a GitHub issue with details
