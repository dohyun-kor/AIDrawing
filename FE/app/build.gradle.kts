import com.android.build.api.dsl.Packaging


plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt") // kapt 플러그인 추가
}


android {
    namespace = "com.example.gametset"
    compileSdk = 35


    defaultConfig {
        applicationId = "com.example.gametset"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    viewBinding {
        enable = true
    }
    dataBinding {
        enable = true
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }

    packaging {
        resources {
            excludes += "META-INF/spring.tooling"
            excludes += "META-INF/spring.handlers"
            excludes += "META-INF/spring.schemas"
            excludes += "META-INF/spring.license.txt"
            excludes += "META-INF/license.txt"
            excludes += "META-INF/notice.txt"
        }
    }


}

dependencies {
    // Core Android Libraries
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.foundation.android)
    implementation(libs.glide)

    // RecyclerView for displaying word list
    implementation("androidx.recyclerview:recyclerview:1.3.1")

    // Lifecycle and ViewModel for managing UI state
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")

    // Kotlin Coroutines for async operations
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Glide for loading images (Optional, useful for saved drawings)
    implementation("com.github.bumptech.glide:glide:4.16.0")
    kapt("com.github.bumptech.glide:compiler:4.16.0")

    // Room for local database storage (Optional, useful for game progress)
    implementation("androidx.room:room-runtime:2.5.2")
    kapt("androidx.room:room-compiler:2.5.2")

    // lottie implementation
    implementation(libs.airbnb.lottie.compose)

    // Firebase (Optional, for real-time database, authentication, etc.)
//    implementation(platform("com.google.firebase:firebase-bom:32.2.3"))
//    implementation("com.google.firebase:firebase-auth-ktx")
//    implementation("com.google.firebase:firebase-database-ktx")
//    implementation("com.google.firebase:firebase-analytics-ktx")

    // TensorFlow Lite (Optional, for AI-based features like drawing or analysis)
    implementation("org.tensorflow:tensorflow-lite:2.13.0")
    implementation("org.tensorflow:tensorflow-lite-gpu:2.13.0")

    implementation("com.squareup.okhttp3:okhttp:4.9.3") // OkHttp 라이브러리
    implementation("org.java-websocket:Java-WebSocket:1.5.2") // WebSocket 지원
    implementation("org.springframework:spring-websocket:5.3.16") // Spring WebSocket 지원
    implementation("org.springframework:spring-messaging:5.3.16") // Spring Messaging 지원


    // Testing Libraries
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation("com.google.code.gson:gson:2.8.8")  // Gson 라이브러리 추가

    // Retrofit 사용을 위한 설정
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // ViewModel 및 LiveData 라이브러리
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.5.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")

    implementation("androidx.fragment:fragment-ktx:1.3.6")

    implementation("com.google.android.material:material:1.10.0")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.fragment:fragment-ktx:1.6.2")
    implementation("com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:0.9.2")
}
