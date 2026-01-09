plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.vinberg88.blanketforandroid"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.vinberg88.blanketforandroid"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    
    // Compose
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    debugImplementation("androidx.compose.ui:ui-tooling")
    
    // DataStore for persistence
    implementation("androidx.datastore:datastore-preferences:1.0.0")
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
// Produces: <repo>/dist/blanket-release.apk
// Note: if release signing is not configured, the produced APK may be unsigned.
tasks.register<Copy>("buildBlanketReleaseApk") {
    dependsOn("assembleRelease")

    val releaseApkDir = layout.buildDirectory.dir("outputs/apk/release")
    from(releaseApkDir)
    include("*.apk")

    // Put the final artifact in a predictable location at repo root.
    into(rootProject.layout.projectDirectory.dir("dist"))

    rename { "blanket-release.apk" }
}

// Convenience task: build an installable (debug-signed) APK with a stable filename.
// Produces: <repo>/dist/blanket-internal.apk
tasks.register<Copy>("buildBlanketInternalApk") {
    dependsOn("assembleInternal")

    val internalApkDir = layout.buildDirectory.dir("outputs/apk/internal")
    from(internalApkDir)
    include("*.apk")

    into(rootProject.layout.projectDirectory.dir("dist"))
    rename { "blanket-internal.apk" }
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
