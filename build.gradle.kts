// Top-level build file — plugin versions declared here, applied in modules.
// AGP 9 has built-in Kotlin support, so the kotlin-android plugin is gone.
// The Compose compiler plugin must match AGP's bundled Kotlin (2.2.10 for AGP 9.2.0).
plugins {
    id("com.android.application") version "9.3.0" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.10" apply false
}
