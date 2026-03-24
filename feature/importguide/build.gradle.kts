plugins {
    alias(libs.plugins.bookmarker.android.feature)
    alias(libs.plugins.bookmarker.android.library.compose)
    alias(libs.plugins.bookmarker.hilt)
    alias(libs.plugins.bookmarker.android.mvi.orbit)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.hdw.bookmarker.feature.importguide"
}

dependencies {
    with(projects.core) {
        implementation(domain)
        implementation(model)
    }

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.lifecycle.viewmodel.compose)

    implementation(libs.accompanist.drawablepainter)
}
