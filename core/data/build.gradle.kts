plugins {
    alias(libs.plugins.bookmarker.android.library)
    alias(libs.plugins.bookmarker.hilt)
}

android {
    namespace = "com.hdw.bookmarker.core.data"
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.datastore)

    implementation(libs.timber)
    implementation(libs.jsoup)

    testImplementation(libs.kotlinx.coroutines.test)
}
