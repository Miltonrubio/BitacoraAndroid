plugins {
    id("com.android.application")
}

android {
    namespace = "com.bitala.bitacora"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.bitala.bitacora"
        minSdk = 24
        targetSdk = 33
        versionCode = 2
        versionName = "2.0"

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
    buildFeatures {
        viewBinding = true;
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
    implementation ("com.google.android.gms:play-services-location:18.0.0")
    implementation ("androidx.viewpager2:viewpager2:1.0")
    implementation("com.makeramen:roundedimageview:2.3.0")
    implementation ("androidx.core:core-ktx:1.7.0")

    implementation ("com.squareup.picasso:picasso:2.71828")
    implementation("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")
    implementation("com.android.volley:volley:1.2.1")
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.okhttp3:okhttp:4.9.1")

    implementation("androidx.recyclerview:recyclerview-selection:1.1.0")

    implementation ("com.google.android.gms:play-services-maps:17.0.1")
    implementation ("com.google.maps:google-maps-services:0.17.0")
    implementation ("com.google.maps.android:android-maps-utils:2.2.4")
    implementation("com.google.android.gms:play-services-location:21.0.1")


    implementation ("com.itextpdf:itextg:5.5.10")



    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("com.airbnb.android:lottie:6.1.0")

    /*
        implementation ("androidx.paging:paging-runtime:3.0.0")
        implementation ("androidx.lifecycle:lifecycle-viewmodel:2.5.1")
        implementation ("androidx.lifecycle:lifecycle-livedata:2.5.1")
    */

}