plugins {
    alias(libs.plugins.android.application)
    //alias(libs.plugins.jetbrains.kotlin.android)
    id("org.jetbrains.kotlin.android").version("1.9.0")
}

android {
    namespace = "dev.tekofx.biblioteques"
    compileSdk = 34

    defaultConfig {
        applicationId = "dev.tekofx.biblioteques"
        minSdk = 26
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
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.2"
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        compose = true
    }


}

dependencies {

    // Android Main Libraries
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.material3)

    // SplashScreen
    implementation(libs.androidx.core.splashscreen)

    // Livedata
    implementation(libs.androidx.runtime.livedata)

    // Navigation
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)

    // Used in Map.kt
    implementation(libs.androidx.appcompat)

    // Animation
    implementation(libs.androidx.animation.core.android)


    ///////////////////////////// Other Libraries /////////////////////////////

    // Open Street Maps
    implementation(libs.osm.android.compose)
    implementation(libs.osmdroid.android)

    // Coil - Async Image Loading
    implementation(libs.coil3.coil.compose)
    implementation(libs.coil.network.okhttp)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // DataStore
    implementation(libs.androidx.datastore.preferences)

    // Ksoup
    implementation(libs.ksoup)
    implementation(libs.ksoup.network)
    implementation(libs.androidx.animation.core.android)
    implementation(libs.androidx.animation.core.android)


    ///////////////////////////// Testing Libraries /////////////////////////////
    // Android Testing
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Testing
    testImplementation(libs.robolectric)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    testImplementation(libs.converter.scalars)
    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockwebserver)
    testImplementation(libs.converter.scalars)
    debugImplementation(libs.androidx.ui.tooling)


}