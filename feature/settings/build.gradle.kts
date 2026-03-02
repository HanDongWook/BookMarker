plugins {
    alias(libs.plugins.bookmarker.android.feature)
    alias(libs.plugins.bookmarker.android.library.compose)
    alias(libs.plugins.bookmarker.android.mvi.mavericks)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.hdw.bookmarker.feature.settings"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.model)

    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.appcompat)

    implementation(libs.coil.compose)

    implementation(libs.aboutlibraries.core)
    implementation(libs.aboutlibraries.compose.m3)
}
