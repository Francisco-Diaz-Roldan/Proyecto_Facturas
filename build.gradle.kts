// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.3" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("org.jetbrains.kotlin.kapt") version "2.0.0-Beta1" apply false //Necesario para Room
    id("com.google.dagger.hilt.android") version "2.46" apply false //Necesario para dagger hilt(retrofit)
    id("org.jetbrains.kotlin.plugin.parcelize") version "1.9.20" apply false//Necesario para Parcelice
}