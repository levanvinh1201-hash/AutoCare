
plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.autocare"

    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.autocare"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    // Android
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.espresso.web)
    implementation(libs.firebase.crashlytics.buildtools)
    implementation(libs.googleid)
    implementation(libs.material)

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:34.19.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")

    // Firebase AI Logic - Gemini AI
    implementation("com.google.firebase:firebase-ai")

    // Firebase App Check (dùng khi phát triển)
    implementation("com.google.firebase:firebase-appcheck-debug")

    // Thư viện hỗ trợ Firebase AI Logic với Java
    implementation("com.google.guava:guava:31.0.1-android")
    implementation("org.reactivestreams:reactive-streams:1.0.4")

    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // Google Maps
    implementation("com.google.android.gms:play-services-maps:19.2.0")

    // Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // Google Sign-In
    implementation("androidx.credentials:credentials:1.3.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.3.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
}
