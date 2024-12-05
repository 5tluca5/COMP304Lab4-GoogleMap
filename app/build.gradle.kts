plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt") apply true
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")

}

//secrets {
//    // To add your Maps API key to this project:
//    // 1. If the secrets.properties file does not exist, create it in the same folder as the local.properties file.
//    // 2. Add this line, where YOUR_API_KEY is your API key:
//    //        MAPS_API_KEY=YOUR_API_KEY
//    propertiesFileName = "secrets.properties"
//
//    // A properties file containing default secret values. This file can be
//    // checked in version control.
//    defaultPropertiesFileName = "local.defaults.properties"
//
//    // Configure which keys should be ignored by the plugin by providing regular expressions.
//    // "sdk.dir" is ignored by default.
//    ignoreList.add("keyToIgnore") // Ignore the key "keyToIgnore"
//    ignoreList.add("sdk.*")       // Ignore all keys matching the regexp "sdk.*"
//}

android {
    namespace = "com.tszhim.tszhimng_comp304lab4_ex1"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.tszhim.tszhimng_comp304lab4_ex1"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    // Google Map
    implementation ("com.google.maps.android:maps-compose:2.11.4")
    implementation ("com.google.maps.android:maps-compose-utils:6.2.1")
    implementation ("com.google.maps.android:maps-compose-widgets:6.2.1")
    implementation ("com.google.android.gms:play-services-location:21.0.1")
    implementation ("androidx.activity:activity-compose:1.3.0-alpha06")

    val work_version = "2.9.1"
    implementation("androidx.work:work-runtime-ktx:$work_version")

    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    kapt("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}