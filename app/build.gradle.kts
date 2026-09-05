plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.alpha.voiceassistant"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.alpha.voiceassistant"
        minSdk = 26          // TileService requires API 24+; 26 keeps things simple
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

repositories {
    google()
    mavenCentral()
    // Spotify App Remote SDK is NOT on Maven Central — hosted on JitPack
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.activity:activity-ktx:1.8.2")
    implementation("androidx.appcompat:appcompat:1.6.1")

    // Spotify App Remote SDK
    implementation("com.spotify.android:auth:2.1.1")
    implementation("com.github.spotify:android-sdk:0.8.0") // App Remote, via JitPack
}