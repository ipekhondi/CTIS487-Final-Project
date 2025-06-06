plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android") version "2.50"
    id("com.google.gms.google-services")
}

android {
    namespace = "com.ctis487_fp.finalproject"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.ctis487_fp.finalproject"
        minSdk = 34
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.1"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")

    // Android dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.espresso.core)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Hilt
    implementation("com.google.dagger:hilt-android:2.50")
    kapt("com.google.dagger:hilt-android-compiler:2.50")

    // Glide
    implementation("com.github.bumptech.glide:glide:4.12.0")
    kapt("com.github.bumptech.glide:compiler:4.12.0") // Use kapt for Kotlin

    // Cloudinary
    implementation ("com.cloudinary:kotlin-url-gen:1.7.0")

    // Picasso
    implementation ("com.squareup.picasso:picasso:2.8")

    val room_version = "2.6.0"
    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")
    kapt ("androidx.room:room-compiler:$room_version")

    implementation ("com.squareup.okhttp3:okhttp:4.9.3") // HTTP requests
    implementation ("com.google.code.gson:gson:2.9.0")  // JSON parsing
    implementation ("com.github.bumptech.glide:glide:4.12.0") // Image loading
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")
    implementation ("androidx.work:work-runtime-ktx:2.x.x")
    implementation ("androidx.work:work-runtime-ktx:2.8.0")
    // Gson Converter
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")



    // Lifecycle components
    implementation( "androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation( "androidx.lifecycle:lifecycle-common-java8:2.2.0")
    implementation( "androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.2..0")

    // Kotlin components
    implementation( "org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.72")
    api ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.5")
    api ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.5")

    val worker_version="2.8.1"
    implementation ("androidx.work:work-runtime:$worker_version")


    implementation ("com.google.android.material:material:1.6.0")

    implementation ("com.airbnb.android:lottie:6.0.0")

    implementation ("androidx.room:room-runtime:2.5.0")
    kapt ("androidx.room:room-compiler:2.5.0")
    implementation ("com.google.code.gson:gson:2.10")
}