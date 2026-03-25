plugins {
    alias(libs.plugins.bookmarker.android.feature)
    alias(libs.plugins.bookmarker.android.library.compose)
    alias(libs.plugins.bookmarker.android.mvi.circuit)
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "com.hdw.bookmarker.feature.trends"
}

dependencies {
    with(projects.core) {
        implementation(domain)
        implementation(model)
    }

    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)

    implementation(libs.timber)
}
