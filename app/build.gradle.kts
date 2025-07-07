plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
    alias(libs.plugins.google.services)
    id("com.google.firebase.crashlytics")
    id("androidx.navigation.safeargs.kotlin")
    alias(libs.plugins.hilt)
}

val appName = "es.upsa.mimo.gamercollection"

val versionMajor = 2
val versionMinor = 4
val versionPatch = 1
val versionBuild = 0 // bump for dogfood builds, public betas, etc.

android {

    namespace = appName
    compileSdk = libs.versions.sdk.compile.get().toInt()

    signingConfigs {
        create("release") {
            if (project.hasProperty("GAMER_COLLECTION_STORE_FILE")) {
                storeFile = file(properties["GAMER_COLLECTION_STORE_FILE"] as String)
                storePassword = properties["GAMER_COLLECTION_STORE_PASSWORD"] as String
                keyAlias = properties["GAMER_COLLECTION_KEY_ALIAS"] as String
                keyPassword = properties["GAMER_COLLECTION_KEY_PASSWORD"] as String
            }
        }

    }

    defaultConfig {

        applicationId = appName
        minSdk = libs.versions.sdk.min.get().toInt()
        targetSdk = libs.versions.sdk.target.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        versionCode = versionMajor * 100000 + versionMinor * 1000 + versionPatch * 10 + versionBuild
        versionName = "${versionMajor}.${versionMinor}.${versionPatch}"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
            manifestPlaceholders["appName"] = "@string/app_name"
        }
        //This is not necessary because debug type is always include in the project by default,
        //but I include it because I want to set the suffix
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = ".debug"
            manifestPlaceholders["appName"] = "Gamer Collection - Pre"
        }
    }

    buildFeatures {
        buildConfig = true
        dataBinding = true
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.versions.jdk.get())
        targetCompatibility = JavaVersion.toVersion(libs.versions.jdk.get())
    }
    kotlinOptions {
        jvmTarget = libs.versions.jdk.get()
    }

    kapt {
        correctErrorTypes = true
    }
}

dependencies {

    //Android
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.fragment.ktx)
    implementation(libs.material)
    implementation(libs.legacy.support.v4)
    implementation(libs.multidex)

    //MVVM & LiveData
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)

    //Navigation
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)

    //Google Play Services
    implementation(libs.play.services.location)
    implementation(libs.play.services.maps)

    //Gson
    implementation(libs.gson)

    //Retrofit
    implementation(libs.bundles.retrofit)

    //Picasso
    implementation(libs.picasso)

    //Material RatingBar
    implementation(libs.materialratingbar)

    //BlurImageView
    implementation(libs.blurImageView)

    //Room
    implementation(libs.room.runtime)
    kapt(libs.room.compiler)
    implementation(libs.room.ktx)

    //Coroutines
    implementation(libs.kotlinx.coroutines.android)

    //Security
    implementation(libs.security.crypto)

    //Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)

    //Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)

    //Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
