import java.util.Properties

plugins {
    alias(libs.plugins.bookmarker.android.application)
    alias(libs.plugins.bookmarker.android.application.compose)
    alias(libs.plugins.bookmarker.hilt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.aboutlibraries)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
}

val versionProperties =
    Properties().apply {
        val versionFile = rootProject.file("version.properties")
        if (versionFile.exists()) {
            versionFile.inputStream().use(::load)
        }
    }

val appVersionCode = versionProperties.getProperty("VERSION_CODE")?.toIntOrNull() ?: 1
val appVersionName = versionProperties.getProperty("VERSION_NAME") ?: "1.0.0"

android {
    namespace = "com.hdw.bookmarker"

    defaultConfig {
        applicationId = "com.hdw.bookmarker"
        versionCode = appVersionCode
        versionName = appVersionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

aboutLibraries {
    export {
        outputFile = file("src/main/res/raw/aboutlibraries.json")
    }
}

tasks.named("preBuild") {
    dependsOn("exportLibraryDefinitions")
}

dependencies {
    // feature
    implementation(projects.feature.home)
    implementation(projects.feature.settings)

    // core
    implementation(projects.core.data)
    implementation(projects.core.designsystem)
    implementation(projects.core.navigation)
    implementation(projects.core.ui)

    implementation(libs.androidx.startup)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.foundation)

    implementation(libs.androidx.navigation.compose)

    implementation(libs.mavericks.core)

    implementation(libs.timber)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.perf)

    debugRuntimeOnly(libs.leakcanary.android)
    debugRuntimeOnly(libs.androidx.compose.ui.test.manifest)
}
