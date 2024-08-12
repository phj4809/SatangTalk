// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.5.2" apply true
    id("org.jetbrains.kotlin.android") version "1.9.20" apply true
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.0.1" apply false
    id("org.jetbrains.kotlin.kapt") version "1.8.20" apply false
}

android {
    namespace = "com.example.satangtalk"
    compileSdk = 34
}