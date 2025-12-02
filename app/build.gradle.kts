plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.paw_help"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.paw_help"
        minSdk = 24
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

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.cardview)
    implementation(libs.recyclerview)

    // Google Play Services for Location
    implementation("com.google.android.gms:play-services-location:21.0.1")
    
    // CoordinatorLayout for better scrolling behavior
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.2.0")
    
    // ViewPager2 for better UI
    implementation("androidx.viewpager2:viewpager2:1.0.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}