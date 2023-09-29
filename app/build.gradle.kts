plugins {
    id("com.android.application")
    id("androidx.navigation.safeargs")
}

android {
    namespace = "com.example.carapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.carapp"
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    val fragmentVersion = "1.5.7"
    implementation("androidx.fragment:fragment:$fragmentVersion")
    val navVersion = "2.5.0"
    // Java Lang Support
    implementation("androidx.navigation:navigation-fragment:$navVersion")
    implementation("androidx.navigation:navigation-ui:$navVersion")
    // OkHTTP API Helper
    val okHTTPVersion = "4.9.1"
    implementation("com.squareup.okhttp3:okhttp:$okHTTPVersion" )
    implementation("com.github.weliem:blessed-android:2.4.2")
    implementation("pl.droidsonroids.gif:android-gif-drawable:1.2.22")
}