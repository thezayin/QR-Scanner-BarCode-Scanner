plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.serialization.json)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.firebase.firebase-perf")
    id("com.google.firebase.appdistribution")
    id("kotlin-kapt")
}

android {
    namespace = "com.thezayin.qrscanner"
    compileSdk = libs.versions.compileSdkVersion.get().toInt()

    defaultConfig {
        applicationId = "com.thezayin.qrscanner"
        minSdk = libs.versions.minSdkVersion.get().toInt()
        targetSdk = libs.versions.targetSdkVersion.get().toInt()
        versionCode = 2
        versionName = "1.0.0.2"

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
    androidResources {
        generateLocaleConfig = true
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        dataBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation(project(":scanner"))
    implementation(project(":generate"))
    implementation(project(":start-up"))
    implementation(project(":history"))
    implementation(project(":premium"))
    implementation(project(":common:values"))
    implementation(project(":databases"))
    implementation(project(":common:framework"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation (libs.billing)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //viewmodel and livedata
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.common.java8)

    //koin dependency injection
    implementation(libs.koin.core)
    implementation(libs.koin.compose)
    implementation(libs.koin.android)
    testImplementation(libs.koin.test.junit4)
    implementation(libs.koin.androidx.navigation)

    //serialization and ktor
    implementation(libs.kotlinx.serialization.json)

    //navigation
    implementation(libs.androidx.navigation.compose)

    //firebase
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.perf)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.inappmessaging.display)
    implementation(libs.firebase.config)
    implementation(libs.jsoup.jsoup)

    implementation(libs.timber)
    implementation(libs.androidx.multidex)
    implementation(libs.grizzly)
    implementation(libs.accompanist.permissions)
    implementation(libs.accompanist.navigation.animation)

    implementation (libs.user.messaging.platform)
    //Funsol Billing
    implementation (libs.billing)
    implementation (libs.funsol.billing.helper)

    implementation (libs.glide)
    annotationProcessor (libs.compiler)

}