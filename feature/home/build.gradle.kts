plugins {
    alias(libs.plugins.bookmarker.android.feature)
    alias(libs.plugins.bookmarker.android.library.compose)
    alias(libs.plugins.bookmarker.hilt)
    alias(libs.plugins.bookmarker.android.mvi.orbit)
}

android {
    namespace = "com.hdw.bookmarker.feature.home"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.model)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)

    implementation(libs.accompanist.drawablepainter)
    implementation(libs.coil.compose)

    implementation(libs.timber)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
