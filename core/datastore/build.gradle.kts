plugins {
    alias(libs.plugins.bookmarker.android.library)
    alias(libs.plugins.bookmarker.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.hdw.bookmarker.core.datastore"
}

dependencies {
    implementation(projects.core.model)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.datastore)
    implementation(libs.androidx.datastore.preferences)

    implementation(libs.kotlinx.serialization.protobuf)
}
