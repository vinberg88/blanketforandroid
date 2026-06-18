# Play Store Release Notes

This file tracks the release checklist for publishing Blanket for Android on Google Play.

## 1. Target SDK

Google Play requires new apps and app updates submitted from 2025-08-31 to target Android 15 / API level 35 or higher.

Current project status:

- `compileSdk = 35`
- `targetSdk = 35`
- Local SDK requirement: install `platforms;android-35`

Useful install command:

```bash
sdkmanager --install "platforms;android-35" "build-tools;35.0.0"
```

## 2. Privacy Policy and Data Safety

Privacy policy file:

- [`../PRIVACY_POLICY.md`](../PRIVACY_POLICY.md)

Suggested Google Play Data Safety answers:

- Data collected: No
- Data shared: No
- Data processed ephemerally: Not applicable
- User account creation: No
- Users can request deletion: Not applicable because no user data is collected by the developer
- Data encrypted in transit: Not applicable because app data is not transmitted
- App category note: Imported audio files remain local on the user's device and are selected through Android's system document picker

Manual Play Console step:

- Add a public privacy policy URL. If GitHub is used, point Play Console to the rendered `PRIVACY_POLICY.md` page on the default branch.

## 3. Store Listing Assets

Prepared assets:

- `docs/play-store/feature-graphic.png` - 1024 x 500 feature graphic
- `docs/play-store/listing.md` - app name, short description, full description, and screenshot checklist

Still needed manually in Play Console:

- Phone screenshots from a real device or emulator
- 512 x 512 high-resolution app icon upload, if Play Console does not reuse the launcher icon

## 4. Release Signing

Do not commit release keys.

Create a local upload key:

```bash
keytool -genkeypair \
  -v \
  -keystore blanket-upload-key.jks \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -alias blanket
```

Create a local `keystore.properties` file in the repository root:

```properties
storeFile=blanket-upload-key.jks
storePassword=YOUR_STORE_PASSWORD
keyAlias=blanket
keyPassword=YOUR_KEY_PASSWORD
```

The Gradle release build reads `keystore.properties` automatically when it exists. The file and keystore are ignored by Git.

## 5. Build Android App Bundle

Google Play should receive an Android App Bundle (`.aab`) for production release.

Build:

```bash
./gradlew :app:buildBlanketReleaseBundle
```

Output:

```text
dist/blanket.aab
```

For local install testing, use the internal APK:

```bash
./gradlew :app:buildBlanketInternalApk
```

## 6. In-App About and Credits

The app includes an About dialog from the bottom control bar.

It covers:

- App name and version
- Android credit
- Original Blanket credit
- Built-in sound licensing note
- Local-only imported sounds note
- No ads, accounts, analytics, or tracking

## 7. Dependabot and Security

Dependabot is enabled for:

- Gradle dependencies
- GitHub Actions

Manual security checklist before production:

- Review GitHub Dependabot alerts on the default branch
- Merge or test Dependabot PRs one at a time
- Run `./gradlew :app:compileDebugKotlin --no-daemon`
- Run `./gradlew :app:buildBlanketReleaseBundle --no-daemon`
- Test imported custom audio on a real Android device
