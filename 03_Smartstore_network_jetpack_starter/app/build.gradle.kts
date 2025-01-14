plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id ("com.google.gms.google-services") // 구글 서비스
    id ("org.jetbrains.kotlin.kapt")
}


android {
    namespace = "com.ssafy.smartstore_jetpack"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ssafy.smartstore_jetpack"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        named("release"){
            isMinifyEnabled = false
            setProguardFiles(listOf(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"))
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    dataBinding {
        enable = true
    }
    viewBinding{
        enable = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation ("nl.dionsegijn:konfetti-xml:2.0.4")

    //파도 웨이브 설정
    implementation ("com.github.orbitalsonic:Sonic-Water-Wave-Animation:1.0.0")
    implementation ("com.airbnb.android:lottie:5.0.3")  // 최신 버전 확인

    // RecyclerView
    implementation ("androidx.recyclerview:recyclerview:1.3.2")

    //indicator
    implementation ("me.relex:circleindicator:2.1.6")

    // FCM 사용 위한 plugins
    implementation(platform("com.google.firebase:firebase-bom:32.3.1"))
    implementation ("com.google.firebase:firebase-messaging-ktx")

    // Beacon 사용위한 Dependency 추가
    //Android beacon Library. https://github.com/AltBeacon/android-beacon-library
    implementation ("org.altbeacon:android-beacon-library:2.19")

    // https://github.com/square/retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    // https://github.com/square/okhttp
    implementation ("com.squareup.okhttp3:okhttp:4.9.0")

    // https://github.com/square/retrofit/tree/master/retrofit-converters/gson
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    // https://github.com/square/okhttp/tree/master/okhttp-logging-interceptor
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.0")

    // Glide 사용
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")

    // Google map API
    implementation ("com.google.android.gms:play-services-maps:18.2.0")
    implementation ("com.google.android.gms:play-services-location:21.2.0")


    //framework ktx dependency 추가
    implementation ("androidx.fragment:fragment-ktx:1.6.2")

    implementation ("com.airbnb.android:lottie:5.0.0")


    implementation ("com.google.android.material:material:1.9.0")
    implementation(kotlin("script-runtime"))

}
