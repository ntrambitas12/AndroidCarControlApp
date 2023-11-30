plugins {
    id("com.android.application")

    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")
    id("androidx.navigation.safeargs")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.gms:play-services-maps:18.1.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
//    implementation("androidx.databinding:databinding-runtime:8.1.4")
//    implementation("androidx.databinding:library:3.2.0-alpha11")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    val fragmentVersion = "1.6.1"
    implementation("androidx.fragment:fragment:$fragmentVersion")
    val navVersion = "2.7.4"
    // Java Lang Support
    implementation("androidx.navigation:navigation-fragment:$navVersion")
    implementation("androidx.navigation:navigation-ui:$navVersion")

    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:32.4.0"))

    // TODO: Add the dependencies for Firebase product we want to use
    // When using the BoM, don't specify versions in Firebase dependencies
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-database")


    // OkHTTP API Helper
    val okHTTPVersion = "4.9.1"
    implementation("com.squareup.okhttp3:okhttp:$okHTTPVersion" )
    implementation("com.github.weliem:blessed-android:2.4.2")
    implementation("pl.droidsonroids.gif:android-gif-drawable:1.2.22")
    implementation("com.github.bumptech.glide:glide:4.11.0")

    annotationProcessor("com.github.bumptech.glide:compiler:4.11.0")

    // Testing
    androidTestImplementation("androidx.fragment:fragment-testing:1.4.0")
    androidTestImplementation("androidx.test:runner:1.4.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation ("androidx.test:rules:1.4.0")
    androidTestImplementation ("androidx.test.uiautomator:uiautomator:2.2.0")

    //Jsoup for web Scraping
    implementation("org.jsoup:jsoup:1.16.1")

    //Glide
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")

}


