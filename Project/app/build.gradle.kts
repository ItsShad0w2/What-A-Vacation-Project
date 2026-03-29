import java.io.FileInputStream
import java.util.Properties


plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

val localProperties = Properties()
val propertiesFile = rootProject.file("local.properties")
if(propertiesFile.exists())
{
    localProperties.load(FileInputStream(propertiesFile))
}

android {
    namespace = "com.example.what_a_vacation_project"
    compileSdk {
        version = release(36)
    }

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.example.what_a_vacation_project"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        buildConfigField ("String", "GeminiAPIKey", "\"${localProperties.getProperty("GeminiAPIKey")}\"")
        buildConfigField ("String", "GooglePlacesAPIKey", "\"${localProperties.getProperty("GooglePlacesAPIKey")}\"")
        manifestPlaceholders["GoogleMapsAPIKey"] = "${localProperties.getProperty("GoogleMapsAPIKey")}"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.generativeai)
    implementation(libs.recyclerview)
    implementation(libs.gson);
    implementation(libs.google.maps)
    implementation("com.google.android.libraries.places:places:5.1.1")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation(libs.protolite.well.known.types)
    implementation(libs.firebase.crashlytics.buildtools)
    implementation(libs.work.runtime)
    implementation(libs.concurrent.futures)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
