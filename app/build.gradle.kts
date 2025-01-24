plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.mavenPublish) // Maven Publish 플러그인

}

android {
    namespace = "com.lodong.poen"
    compileSdk = 35


    defaultConfig {
        applicationId = "com.lodong.poen"
        minSdk = 26
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }
    packaging {
        resources {
            excludes+="**/*"
        }
    }
}

dependencies {

    implementation(libs.navigation.compose)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")





    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    implementation(libs.core.ktx.v1131)
    implementation(libs.common)
    implementation(libs.camera.camera2)
    implementation(project(":bluetooth-module"))
    implementation(libs.firebase.appdistribution.gradle)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
//    implementation("com.lodong.android:bluetooth-module:1.0.4")
    implementation("com.lodong.android:utils:1.0.9") //여기에도
    implementation("com.squareup.retrofit2:retrofit:2.9.0") // Retrofit
    implementation("com.squareup.retrofit2:converter-gson:2.9.0") // Gson
    // Accompanist Permissions
    implementation("com.google.accompanist:accompanist-permissions:0.30.1") // 최신 버전 확인

    // Compose Material3
    implementation("androidx.compose.material3:material3:1.2.0") // 최신 버전 확인

    implementation("com.google.mlkit:barcode-scanning:17.0.3")

    implementation ("com.github.bumptech.glide:compose:1.0.0-alpha.3")

    implementation("androidx.camera:camera-core:1.4.0-alpha02")
    implementation("androidx.camera:camera-view:1.4.0-alpha02")
    implementation("androidx.camera:camera-lifecycle:1.4.0-alpha02")
//    implementation("com.google.mlkit:barcode-scanning:17.0.2")
    implementation ("io.coil-kt:coil-compose:2.2.2")

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.foundation:foundation")
    implementation("com.jakewharton.timber:timber:5.0.1")
    implementation("com.google.accompanist:accompanist-permissions:0.32.0")

    debugImplementation("androidx.compose.ui:ui-tooling")

    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")


    implementation ("com.google.guava:guava:31.1-android")
    implementation ("androidx.concurrent:concurrent-futures-ktx:1.1.0")
    implementation ("org.nanohttpd:nanohttpd:2.3.1")


    // Hilt를 사용하는 경우
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    implementation ("androidx.webkit:webkit:1.6.0")

}
