plugins {
    alias(libs.plugins.bookmarker.android.library)
}

android {
    namespace = "com.hdw.bookmarker.core.model"
}

dependencies {
    implementation(libs.timber)
}
