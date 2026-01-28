plugins {
    alias(libs.plugins.bookmarker.android.library)
    alias(libs.plugins.bookmarker.hilt)
}

android {
    namespace = "com.hdw.bookmarker.data"
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.domain)

    implementation(libs.androidx.core.ktx)

    implementation(libs.timber)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}