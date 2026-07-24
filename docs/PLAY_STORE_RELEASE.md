# Play Store Release Notes

This file tracks the release checklist for publishing Blanket for Android on Google Play.

## 1. Target SDK

Google Play requires new apps and app updates submitted from 2026-08-31 to target Android 16 / API level 36 or higher.

Current project status:

- `compileSdk = 36`
- `targetSdk = 36`
- `applicationId = space.manus.blanket.android.t20260402202534`
- `versionCode = 10001`
- `versionName = 1.1.0`
- Local SDK requirement: install `platforms;android-36`

Useful install command:

```bash
sdkmanager --install "platforms;android-36" "build-tools;36.0.0"
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
- Permissions declaration: No sensitive or restricted Android permissions are requested
- Ads declaration: No
- App access: All functionality is available without an account or login

Manual Play Console step:

- Add a public privacy policy URL. If GitHub is used, point Play Console to the rendered `PRIVACY_POLICY.md` page on the default branch.

## 3. Store Listing Assets

Prepared assets:

- `docs/play-store/feature-graphic.png` - 1024 x 500 feature graphic
- `docs/play-store/listing.md` - app name, short description, full description, and screenshot checklist

Still needed manually in Play Console:

- Phone screenshots from a real device or emulator
- `docs/play-store/icon-512.png` is the 512 x 512 high-resolution store icon

## 4. Release Signing

Do not commit release keys.

Because Blanket already exists in Google Play testing, first compare the SHA-256
fingerprint under **Play Console > App integrity > Upload key certificate** with
the key you intend to use. Existing releases must use the registered upload key.

If the original upload key is unavailable, create a new upload key and request an
upload-key reset in Play Console.

### Windows and Android Studio

WSL and Ubuntu are not required. In Android Studio select:

1. **Build > Generate Signed Bundle / APK**
2. **Android App Bundle**
3. **Create new**
4. Save the key outside the repository, for example:
   `C:\Users\YOUR_NAME\AndroidKeys\blanket-upload-key.jks`
5. Use the alias `blanket-upload` and a validity of at least 10,000 days

Export the public upload certificate from PowerShell:

```powershell
& "C:\Program Files\Android\Android Studio\jbr\bin\keytool.exe" `
  -export -rfc `
  -keystore "$env:USERPROFILE\AndroidKeys\blanket-upload-key.jks" `
  -alias blanket-upload `
  -file "$env:USERPROFILE\AndroidKeys\blanket-upload-certificate.pem"
```

Only the `.pem` certificate is uploaded when requesting an upload-key reset.
The private `.jks` file and its passwords must remain secret.

### Command-line alternative

Create a local upload key:

```bash
keytool -genkeypair \
  -v \
  -keystore blanket-upload-key.jks \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -alias blanket-upload
```

Create a local `keystore.properties` file in the repository root:

```properties
storeFile=blanket-upload-key.jks
storePassword=YOUR_STORE_PASSWORD
keyAlias=blanket-upload
keyPassword=YOUR_KEY_PASSWORD
```

The Gradle release build reads `keystore.properties` automatically when it
exists. The file and keystore are ignored by Git.

### Signed release through GitHub Actions

Add these repository secrets under **Settings > Secrets and variables >
Actions**:

- `BLANKET_UPLOAD_KEYSTORE_BASE64`
- `BLANKET_KEYSTORE_PASSWORD`
- `BLANKET_KEY_ALIAS` (`blanket-upload`)
- `BLANKET_KEY_PASSWORD`

Create the Base64 value in Windows PowerShell:

```powershell
[Convert]::ToBase64String(
  [IO.File]::ReadAllBytes(
    "$env:USERPROFILE\AndroidKeys\blanket-upload-key.jks"
  )
) | Set-Clipboard
```

After the secrets are configured, open **Actions > Signed Play release > Run
workflow**. The workflow restores the private key only inside the temporary
GitHub runner, builds `dist/blanket.aab`, verifies its signature, and publishes
the `blanket-signed-play-aab` artifact. The signed-release workflow is manual so
release secrets are not exposed to normal pull-request builds.

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

## 6. Android 15 foreground-service warning

Play Console can retain the warning **Restricted foreground service types** for
the older Manus test bundle with `versionCode 10000`.

The native Blanket `versionCode 10001` does not declare:

- `BOOT_COMPLETED`
- `RECEIVE_BOOT_COMPLETED`
- a foreground service
- a foreground-service permission

Upload and roll out the signed `10001` bundle to the same testing track. If Play
still lists the warning, open **View details > Affected bundles** and confirm
whether it points to historical bundle `10000` or the new `10001`.

## 7. In-App About and Credits

The app includes an About dialog from the bottom control bar.

It covers:

- App name and version
- Android credit
- Original Blanket credit
- Built-in sound licensing note
- Local-only imported sounds note
- No ads, accounts, analytics, or tracking

## 8. Dependabot and Security

Dependabot is enabled for:

- Gradle dependencies
- GitHub Actions

Manual security checklist before production:

- Review GitHub Dependabot alerts on the default branch
- Merge or test Dependabot PRs one at a time
- Run `./gradlew lintDebug testDebugUnitTest --no-daemon`
- Run `./gradlew :app:buildBlanketReleaseBundle --no-daemon`
- Test imported custom audio on a real Android device

## 8. Play Console declarations

Complete these manually in Play Console:

- App category: Music & Audio
- Ads: No
- App access: No restricted access
- Target audience: Select the actual intended age groups; the app is not specifically directed to children
- Content rating: Complete the IARC questionnaire as a utility/audio app
- Data safety: No data collected and no data shared
- Government, health, finance, news, VPN and social declarations: Not applicable
- Upload the signed `dist/blanket.aab` to an internal testing track first
- Add at least one tester, install from Google Play, and verify playback, looping, import and the sleep timer

The app uses Android's system document picker and therefore needs no broad storage or media permission.

For personal developer accounts created after 13 November 2023, Google currently requires a closed test with at least 12 opted-in testers for 14 continuous days before production access can be requested.
