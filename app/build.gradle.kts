plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs")
}

android {
    namespace = "com.example.recipeapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.recipeapp"
        minSdk = 27
        //noinspection OldTargetApi
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Navigation Component
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)

    // Room
    implementation(libs.androidx.room.runtime)
    annotationProcessor(libs.androidx.room.compiler)

    // To use Kotlin annotation processing tool (kapt)
    //noinspection KaptUsageInsteadOfKsp

   kapt(libs.androidx.room.compiler)


    // optional - Kotlin Extensions and Coroutines support for Room
    implementation(libs.androidx.room.ktx)


    // Retrofit (API Calls)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)


    // Glide (for loading images from URL)
    implementation(libs.glide)


    // ViewModel and LiveData
    implementation(libs.androidx.lifecycle.viewmodel.ktx.v292)
    implementation(libs.androidx.lifecycle.livedata.ktx.v292)
    kapt(libs.androidx.lifecycle.compiler)

    //for video
    implementation("com.pierfrancescosoffritti.androidyoutubeplayer:core:12.1.0")


}