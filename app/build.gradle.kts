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
