import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension
import javax.xml.parsers.DocumentBuilderFactory

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

fun getSecretValue(name: String): String =
    try {
        val secretsFile = file("./secrets/secrets.xml")
        val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(secretsFile)
        val nodes = doc.getElementsByTagName("string")
        (0 until nodes.length)
            .map { nodes.item(it) }
            .first { it.attributes.getNamedItem("name")?.nodeValue == name }
            .textContent
    } catch (_: Exception) {
        ""
    }

android {
    compileSdk = 36
    namespace = "com.alvaroquintana.adivinaperro"

    defaultConfig {
        applicationId = "com.alvaroquintana.adivinaperro"
        minSdk = 23
        targetSdk = 36
        versionCode = 34
        versionName = "3.0.2"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            keyAlias = findProperty("ADIVINAPERRO_RELEASE_KEY_ALIAS") as? String
            keyPassword = findProperty("ADIVINAPERRO_RELEASE_KEY_PASSWORD") as? String
            storeFile = (findProperty("ADIVINAPERRO_RELEASE_STORE_FILE") as? String)?.let { file(it) }
            storePassword = findProperty("ADIVINAPERRO_RELEASE_STORE_PASSWORD") as? String
        }
    }

    buildTypes {
        debug {
            isJniDebuggable = true
            isDebuggable = true
            isMinifyEnabled = false
            configure<CrashlyticsExtension> { mappingFileUploadEnabled = false }

            resValue("string", "admob_id", getSecretValue("admob_id"))
            resValue("string", "BANNER_GAME", getSecretValue("admob_banner_test_id"))
            resValue("string", "BANNER_INFO", getSecretValue("admob_banner_test_id"))
            resValue("string", "BONIFICADO_GAME", getSecretValue("admob_bonificado_test_id"))
            resValue("string", "BONIFICADO_GAME_OVER", getSecretValue("admob_bonificado_test_id"))
            resValue("string", "INTERSTICIAL_GAME_OVER", "ca-app-pub-3940256099942544/1033173712")

            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            isDebuggable = false
            ndk { debugSymbolLevel = "FULL" }
            signingConfig = signingConfigs.getByName("release")
            configure<CrashlyticsExtension> { mappingFileUploadEnabled = true }

            resValue("string", "admob_id", getSecretValue("admob_id"))
            resValue("string", "BANNER_GAME", getSecretValue("admob_banner_game"))
            resValue("string", "BANNER_INFO", getSecretValue("admob_banner_info"))
            resValue("string", "BONIFICADO_GAME", getSecretValue("admob_bonificado_game"))
            resValue("string", "BONIFICADO_GAME_OVER", getSecretValue("admob_bonificado_game_over"))
            resValue("string", "INTERSTICIAL_GAME_OVER", getSecretValue("admob_intersticial"))
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        buildConfig = true
        compose = true
        resValues = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":data"))
    implementation(project(":usecases"))

    // Kotlin
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // AndroidX
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.preference.ktx)
    implementation(libs.androidx.swiperefreshlayout)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.database)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.crashlytics)
    implementation(libs.guava.listenablefuture)

    // DI
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.compose.viewmodel)

    // Images
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    // Ads
    implementation(libs.play.services.ads)
    implementation(libs.ump)

    implementation(libs.androidx.material3.window.size.class1)

    // Jetpack Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.material3)
    implementation(libs.compose.material3.window.size)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.navigation.compose)
    debugImplementation(libs.compose.ui.tooling)

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)

    // Android Instrumented Testing
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.compose.ui.test.junit4)
    androidTestImplementation(libs.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.test.runner)
    androidTestImplementation(libs.test.rules)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    debugImplementation(libs.compose.ui.test.manifest)
}

configurations.all {
    exclude(group = "com.google.android.gms", module = "play-services-safetynet")
}
