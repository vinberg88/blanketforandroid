import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
}

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties().apply {
    if (keystorePropertiesFile.exists()) {
        keystorePropertiesFile.inputStream().use { load(it) }
    }
}
val hasReleaseSigning = listOf(
    "storeFile",
    "storePassword",
    "keyAlias",
    "keyPassword"
).all { !keystoreProperties.getProperty(it).isNullOrBlank() }

android {
    namespace = "com.vinberg88.blanketforandroid"
    compileSdk = 36

    defaultConfig {
        applicationId = "space.manus.blanket.android.t20260402202534"
        minSdk = 23
        targetSdk = 36
        versionCode = 10001
        versionName = "1.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            if (hasReleaseSigning) {
                storeFile = rootProject.file(keystoreProperties.getProperty("storeFile"))
                storePassword = keystoreProperties.getProperty("storePassword")
                keyAlias = keystoreProperties.getProperty("keyAlias")
                keyPassword = keystoreProperties.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        release {
            if (hasReleaseSigning) {
                signingConfig = signingConfigs.getByName("release")
            }
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        // Installable build for local testing/CI artifacts without a private keystore.
        // Signed with the default Android debug key.
        create("internal") {
            initWith(getByName("release"))
            signingConfig = signingConfigs.getByName("debug")
            // Helps with installing alongside other variants.
            applicationIdSuffix = ".internal"
            versionNameSuffix = "-internal"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.19.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.14.0")
    
    // Compose
    implementation(platform("androidx.compose:compose-bom:2026.06.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.activity:activity-compose:1.13.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.11.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.11.0")
    debugImplementation("androidx.compose.ui:ui-tooling")
    testImplementation("junit:junit:4.13.2")
    
    // DataStore for persistence
    implementation("androidx.datastore:datastore-preferences:1.2.1")
}

// Convenience task: build a single APK with a stable filename.
// Produces: <repo>/dist/blanket.apk
tasks.register<Copy>("buildBlanketApk") {
    dependsOn("assembleDebug")

    val apkPath = layout.buildDirectory.file("outputs/apk/debug/app-debug.apk")
    from(apkPath)

    // Put the final artifact in a predictable location at repo root.
    into(rootProject.layout.projectDirectory.dir("dist"))

    rename { "blanket.apk" }
}

// Convenience task: build a release APK with a stable filename.
// Produces: <repo>/dist/blanket.apk
// Note: if release signing is not configured, the produced APK may be unsigned.
tasks.register<Copy>("buildBlanketReleaseApk") {
    dependsOn("assembleRelease")

    val releaseApkDir = layout.buildDirectory.dir("outputs/apk/release")
    from(releaseApkDir)
    include("*.apk")

    // Put the final artifact in a predictable location at repo root.
    into(rootProject.layout.projectDirectory.dir("dist"))

    rename { "blanket.apk" }
}

// Convenience task: build a Play Store Android App Bundle with a stable filename.
// Produces: <repo>/dist/blanket.aab
// Add keystore.properties locally before publishing to create a signed release bundle.
tasks.register<Copy>("buildBlanketReleaseBundle") {
    dependsOn("bundleRelease")

    val releaseBundleDir = layout.buildDirectory.dir("outputs/bundle/release")
    from(releaseBundleDir)
    include("*.aab")

    into(rootProject.layout.projectDirectory.dir("dist"))
    rename { "blanket.aab" }
}

// Convenience task: build an installable (debug-signed) APK with a stable filename.
// Produces: <repo>/dist/blanket.apk
tasks.register<Copy>("buildBlanketInternalApk") {
    dependsOn("assembleInternal")

    val internalApkDir = layout.buildDirectory.dir("outputs/apk/internal")
    from(internalApkDir)
    include("*.apk")

    into(rootProject.layout.projectDirectory.dir("dist"))
    rename { "blanket.apk" }
}

// Compatibility task: some CI/scripts expect ':app:validateReleaseSigning'.
// AGP creates signing validation tasks as 'validateSigning<Variant>' (e.g., validateSigningDebug).
// This alias will prefer release validation when available, otherwise it falls back to debug.
val validateReleaseSigning = tasks.register("validateReleaseSigning") {
    group = "verification"
    description = "Compatibility alias for signing validation (prefers release when configured)."
}

afterEvaluate {
    val target = tasks.findByName("validateSigningRelease")
        ?: tasks.findByName("validateSigningInternal")
        ?: tasks.findByName("validateSigningDebug")

    if (target != null) {
        validateReleaseSigning.configure { dependsOn(target) }
    } else {
        logger.warn(
            "No validateSigningRelease/validateSigningDebug tasks found; validateReleaseSigning will do nothing."
        )
    }
}
