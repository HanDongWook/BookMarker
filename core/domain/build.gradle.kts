plugins {
    alias(libs.plugins.bookmarker.android.library)
    alias(libs.plugins.bookmarker.hilt)
}

android {
    namespace = "com.hdw.bookmarker.domain"
}

dependencies {
    api(projects.core.data)
    implementation(projects.core.model)
}
