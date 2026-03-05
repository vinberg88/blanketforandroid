# Azure CI/CD - Building APK Files

This document explains how to build Blanket Android APK files using Azure Pipelines.

## Overview

The project includes an Azure Pipelines configuration (`azure-pipelines.yml`) that automatically builds Android APK files whenever code is pushed to the repository.

## Pipeline Configuration

The Azure Pipelines configuration builds three APK variants:

1. **Debug APK** (`blanket-debug.apk`) - Development build with debugging enabled
2. **Internal APK** (`blanket-internal.apk`) - Debug-signed release build for testing
3. **Release APK** (`blanket-release.apk`) - Production-ready build (unsigned by default)

## How to Use Azure Pipelines

### Setting Up Azure Pipelines

1. **Connect your GitHub repository to Azure DevOps:**
   - Go to [Azure DevOps](https://dev.azure.com/)
   - Create a new project or select an existing one
   - Navigate to Pipelines → Create Pipeline
   - Select GitHub as the source
   - Authorize Azure Pipelines to access your repository
   - Select the `vinberg88/blanketforandroid` repository

2. **Azure Pipelines will automatically detect the `azure-pipelines.yml` file:**
   - The pipeline configuration is already present in the repository root
   - Azure will prompt you to review and run the pipeline
   - Click "Run" to start the first build

### Running the Pipeline

The pipeline automatically triggers on:
- Pushes to the `main` branch
- Pushes to the `develop` branch
- Pull requests targeting the `main` branch

You can also manually trigger a build:
1. Go to Azure DevOps → Pipelines
2. Select the Blanket pipeline
3. Click "Run pipeline"
4. Select the branch you want to build
5. Click "Run"

### Downloading Built APK Files

After the pipeline completes successfully:

1. **Navigate to the completed pipeline run:**
   - Go to Azure DevOps → Pipelines
   - Click on the pipeline run you want to download from

2. **Download the artifacts:**
   - Look for the "Artifacts" section (usually in the top right)
   - Click on "blanket-apks" artifact
   - Click "Download" to get a ZIP file containing all APK variants

3. **Extract and install:**
   - Extract the ZIP file
   - You'll find `blanket-debug.apk`, `blanket-internal.apk`, and `blanket-release.apk`
   - Transfer the APK to your Android device
   - Enable "Install from unknown sources" in Android settings
   - Install the APK

## Pipeline Steps Explained

The Azure Pipelines configuration performs the following steps:

### 1. Environment Setup
- **Checkout repository** - Clones the code from GitHub
- **Set up JDK 17** - Installs Java Development Kit (required for Android builds)
- **Configure Android SDK** - Sets up the Android SDK environment variables
- **Install Android SDK components** - Installs required Android SDK platforms and build tools

### 2. Build APK Variants
The pipeline builds three variants using Gradle convenience tasks:

```bash
# Debug APK (for development)
./gradlew :app:buildBlanketApk
# Output: dist/blanket.apk → copied to blanket-debug.apk

# Internal APK (debug-signed for testing)
./gradlew :app:buildBlanketInternalApk
# Output: dist/blanket-internal.apk

# Release APK (production, may be unsigned)
./gradlew :app:buildBlanketReleaseApk
# Output: dist/blanket-release.apk
```

### 3. Publish Artifacts
- **Copy APKs to staging** - Copies built APK files to Azure's artifact staging directory
- **Publish artifacts** - Makes the APK files available for download as pipeline artifacts

## APK Variants Explained

### Debug APK (`blanket-debug.apk`)
- **Purpose:** Development and debugging
- **Signing:** Signed with Android debug keystore
- **Package ID:** `com.vinberg88.blanketforandroid`
- **Use when:** Testing during development, debugging issues

### Internal APK (`blanket-internal.apk`)
- **Purpose:** Testing release builds without production signing
- **Signing:** Signed with Android debug keystore (installable without release keys)
- **Package ID:** `com.vinberg88.blanketforandroid.internal`
- **Use when:** Testing release configuration, distributing to testers

### Release APK (`blanket-release.apk`)
- **Purpose:** Production distribution
- **Signing:** Unsigned by default (requires keystore configuration)
- **Package ID:** `com.vinberg88.blanketforandroid`
- **Use when:** Publishing to Play Store or distributing to end users

## Configuring Release Signing

By default, the release APK is built without signing. To sign the release APK:

### Option 1: Configure Keystore in Azure Pipelines (Recommended)

1. **Create or use an existing keystore:**
   ```bash
   keytool -genkey -v -keystore blanket-release.keystore \
     -alias blanket -keyalg RSA -keysize 2048 -validity 10000
   ```

2. **Upload keystore to Azure Pipelines:**
   - Go to Azure DevOps → Pipelines → Library
   - Create a new "Secure files"
   - Upload your `.keystore` file

3. **Add signing configuration to pipeline:**
   - Add variables for keystore password, alias, and key password
   - Use the `DownloadSecureFile` task in the pipeline
   - Configure Gradle signing in `app/build.gradle.kts`

### Option 2: Use Google Play App Signing (Recommended for Play Store)

If you're publishing to Google Play Store:
1. Let Google manage your signing key (recommended)
2. Upload an internal/debug-signed APK first
3. Google will re-sign with their managed key

## Troubleshooting

### Build Fails with "SDK not found"
- Check that `ANDROID_SDK_VERSION` and `ANDROID_BUILD_TOOLS_VERSION` in `azure-pipelines.yml` match the versions in `app/build.gradle.kts`

### Build Fails with "Java version mismatch"
- Ensure JDK 17 is being used (required for Android Gradle Plugin 8.2.0)
- Check the `JavaToolInstaller@0` task in the pipeline

### APK Not Found in Artifacts
- Check the pipeline logs for build errors
- Verify that the Gradle tasks completed successfully
- Look for the "Copy APK artifacts" step in the logs

### Release APK Not Signing
- This is expected behavior if release signing is not configured
- Use the internal APK for testing, or configure release signing as described above

## Local vs Azure Builds

### Building Locally (Ubuntu/Linux)

For manual builds on a local machine, follow the instructions in the main README:

```bash
# Clone the repository
git clone https://github.com/vinberg88/blanketforandroid.git
cd blanketforandroid

# Build APK variants
./gradlew :app:buildBlanketApk           # Debug
./gradlew :app:buildBlanketInternalApk   # Internal
./gradlew :app:buildBlanketReleaseApk    # Release

# APKs are in dist/ directory
ls -lh dist/*.apk
```

### Building with Azure Pipelines

Azure Pipelines provide automated builds with:
- ✅ Consistent build environment
- ✅ Automatic builds on every push
- ✅ Artifact storage and version history
- ✅ No need for local Android SDK setup
- ✅ Easy sharing of builds with team members

## Build Times

Typical Azure Pipelines build times:
- **Environment setup:** ~2-3 minutes (JDK + Android SDK)
- **Debug APK:** ~1-2 minutes
- **Internal APK:** ~1-2 minutes
- **Release APK:** ~1-2 minutes
- **Total:** ~5-10 minutes

## Best Practices

1. **Use internal APK for testing** - It's signed and ready to install without keystore configuration
2. **Keep Azure Pipelines up to date** - Regularly review and update SDK versions
3. **Use secure variables** - Never commit keystore passwords or signing credentials
4. **Test locally first** - Run local builds before pushing to ensure quick feedback
5. **Monitor pipeline costs** - Azure Pipelines has free tier limits; monitor your usage

## Related Documentation

- [BUILD_NOTES.md](BUILD_NOTES.md) - General build instructions and project structure
- [GRADLE_CONFIGURATION.md](GRADLE_CONFIGURATION.md) - Gradle configuration details
- [Main README](../README.md) - Project overview and manual build instructions

## Support

For issues with:
- **Azure Pipelines setup:** Check [Azure DevOps Documentation](https://docs.microsoft.com/azure/devops/pipelines/)
- **Android builds:** See [BUILD_NOTES.md](BUILD_NOTES.md)
- **Gradle tasks:** See [GRADLE_CONFIGURATION.md](GRADLE_CONFIGURATION.md)
