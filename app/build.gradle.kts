plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.farmnavi"
    compileSdk = 36

    buildFeatures{
        viewBinding = true
    }

    aaptOptions {
        noCompress ("tflite")
    }

    defaultConfig {
        applicationId = "com.example.farmnavi"
        minSdk = 29
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    // Firebase BoM - manages versions for Firebase libs
    implementation(platform("com.google.firebase:firebase-bom:32.1.0"))

    // Firebase libraries without version numbers, managed by BoM
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")

    // Other libraries with explicit versions as usual
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("androidx.activity:activity-ktx:1.8.0")

    // TensorFlow Lite dependencies
    implementation("org.tensorflow:tensorflow-lite:2.17.0")
    implementation("org.tensorflow:tensorflow-lite-gpu:2.17.0")
    implementation("org.tensorflow:tensorflow-lite-support:0.4.0") {
        exclude(group = "org.tensorflow", module = "tensorflow-lite-api")
        exclude(group = "com.google.ai.edge.litert", module = "litert-api")
    }

    // Lottie Animation
    implementation ("com.airbnb.android:lottie:6.6.3")

    // Retrofit for network requests
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation(libs.google.material)
    implementation(libs.androidx.activity)

    // Testing libraries
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}

