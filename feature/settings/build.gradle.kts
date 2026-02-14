plugins {
    alias(libs.plugins.bookmarker.android.feature)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.hdw.bookmarker.feature.settings"
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)

    implementation(libs.timber)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
