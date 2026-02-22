plugins {
    alias(libs.plugins.bookmarker.android.library)
    alias(libs.plugins.bookmarker.android.library.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.hdw.bookmarker.core.navigation"
}

dependencies {
    implementation(projects.core.common)

    implementation(projects.feature.home)
    implementation(projects.feature.settings)

    implementation(libs.androidx.compose.ui)

    implementation(libs.androidx.navigation.compose)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
