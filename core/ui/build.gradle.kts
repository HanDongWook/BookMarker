plugins {
    alias(libs.plugins.bookmarker.android.library)
    alias(libs.plugins.bookmarker.android.library.compose)
}

android {
    namespace = "com.hdw.bookmarker.core.ui"
}

dependencies {
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)

    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
}
