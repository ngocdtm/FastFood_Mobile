plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.foodnhanh"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.foodnhanh"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    buildFeatures{
        viewBinding = true;
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    //noinspection GradleCompatible
   implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-database:20.3.1")
    implementation("com.google.firebase:firebase-auth:22.3.1")
    implementation("com.google.firebase:firebase-storage:20.3.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.navigation:navigation-ui:2.7.7")
    implementation("com.google.firebase:firebase-firestore:24.10.3")
    implementation("com.google.firebase:firebase-core:21.1.1")

    implementation("androidx.cardview:cardview:1.0.0")
    implementation("com.github.p32929:AndroidEasySQL-Library:1.4.1")
    implementation("de.hdodenhof:circleimageview:3.1.0")

    // FirebaseUI for Firebase Realtime Database
    implementation ("com.firebaseui:firebase-ui-database:8.0.2")
    implementation("androidx.navigation:navigation-fragment:2.7.7")

    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation ("com.makeramen:roundedimageview:2.3.0")
    testImplementation("junit:junit:4.13.2")
    implementation ("androidx.viewpager2:viewpager2:1.0.0")
    implementation ("com.airbnb.android:lottie:4.2.2")
    implementation ("com.github.denzcoskun:ImageSlideshow:0.1.2")
    implementation ("com.scwang.smartrefresh:SmartRefreshLayout:1.1.0")
    implementation ("com.scwang.smartrefresh:SmartRefreshHeader:1.1.0")

}