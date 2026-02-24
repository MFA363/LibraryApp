import org.jetbrains.kotlin.gradle.dsl.JvmTarget

        plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
}
android {
    namespace = "com.example.libraryapp20"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.libraryapp20"
        minSdk = 27
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
}

// Add this block to configure Kotlin compiler options
kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}

dependencies {
        // --- ANDROIDX & MATERIAL UI LIBRARIES ---
        // These are standard libraries for core functionality and UI components.
        implementation(libs.androidx.core.ktx)
        implementation(libs.androidx.appcompat)
        implementation(libs.material)
        implementation(libs.androidx.activity)
        implementation(libs.androidx.constraintlayout)

        // --- FIREBASE LIBRARIES (The Correct Way) ---
        // 1. Import the Firebase Bill of Materials (BOM).
        // This MUST come before other Firebase dependencies. It manages all their versions.
        implementation(platform(libs.firebase.bom))

        // 2. Now add the Firebase libraries you need WITHOUT versions.
        // The BOM will provide the correct, compatible versions for them.
    // For Authentication (You were missing this alias)
    // For Firestore Database
    // For Realtime Database (You were missing this alias)
        implementation("com.google.firebase:firebase-storage") // For Cloud Storage (KTX version is better)
        implementation("com.google.firebase:firebase-analytics") // For Analytics (KTX version is better)
        implementation("com.google.firebase:firebase-crashlytics") // For Crashlytics (KTX version is better)
        implementation("com.google.firebase:firebase-auth")
        implementation ("com.google.firebase:firebase-firestore")
        implementation ("com.google.firebase:firebase-database")

        // --- THIRD-PARTY LIBRARIES ---
        // For displaying circular images
        implementation("de.hdodenhof:circleimageview:3.1.0")

        // For loading images efficiently
        implementation("com.github.bumptech.glide:glide:5.0.5")
        annotationProcessor("com.github.bumptech.glide:compiler:5.0.5")

        // --- TESTING LIBRARIES ---
        testImplementation(libs.junit)
        androidTestImplementation(libs.androidx.junit)
        androidTestImplementation(libs.androidx.espresso.core)
    }
