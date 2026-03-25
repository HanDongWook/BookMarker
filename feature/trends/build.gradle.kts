plugins {
    alias(libs.plugins.bookmarker.android.feature)
    alias(libs.plugins.bookmarker.android.library.compose)
}

android {
    namespace = "com.hdw.bookmarker.feature.trends"
}

dependencies {
    with(projects.core) {
        implementation(ui)
    }

    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
}
