plugins {
    alias(libs.plugins.bookmarker.android.library)
    alias(libs.plugins.bookmarker.hilt)
}

android {
    namespace = "com.hdw.bookmarker.data"
}

dependencies {
    implementation(projects.core.model)

    implementation(libs.androidx.core.ktx)
    implementation(libs.timber)
}
