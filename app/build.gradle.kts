plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.testgiuaky2"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.testgiuaky2"
        minSdk = 24
        targetSdk = 35
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Retrofit for API calls
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)

    // Glide for image loading
    implementation(libs.glide)
    annotationProcessor(libs.glide.compiler)
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation ("com.google.android.material:material:1.4.0")
}