// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {

    val agp_version = "8.11.0"
    val kotlin_version = "2.2.0"
    val navigationVersion = "2.9.1"
    val hilt_version = "2.56.2"

    dependencies {
        classpath("com.android.tools.build:gradle:$agp_version")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
        classpath("com.google.gms:google-services:4.4.3")
        classpath("com.google.firebase:firebase-crashlytics-gradle:3.0.4")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$navigationVersion")
        classpath("com.google.dagger:hilt-android-gradle-plugin:$hilt_version")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

plugins {
   id("com.android.application") version "8.11.0" apply false
   id("org.jetbrains.kotlin.android") version "2.2.0" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
