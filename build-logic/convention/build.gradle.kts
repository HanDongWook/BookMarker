import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "com.hdw.bookmarker.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

dependencies {
    compileOnly(libs.android.gradleApiPlugin)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.spotless.gradlePlugin)
    compileOnly(libs.detekt.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidApplicationCompose") {
            id = libs.plugins.bookmarker.android.application.compose.get().pluginId
            implementationClass = "AndroidApplicationComposeConventionPlugin"
        }
        register("androidApplication") {
            id = libs.plugins.bookmarker.android.application.asProvider().get().pluginId
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = libs.plugins.bookmarker.android.library.asProvider().get().pluginId
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("hilt") {
            id = libs.plugins.bookmarker.hilt.get().pluginId
            implementationClass = "HiltConventionPlugin"
        }
        register("jvmLibrary") {
            id = libs.plugins.bookmarker.jvm.library.get().pluginId
            implementationClass = "JvmLibraryConventionPlugin"
        }
        register("codeQuality") {
            id = libs.plugins.bookmarker.code.quality.get().pluginId
            implementationClass = "CodeQualityConventionPlugin"
        }
        register("androidFeature") {
            id = libs.plugins.bookmarker.android.feature.get().pluginId
            implementationClass = "AndroidFeatureConventionPlugin"
        }
        register("androidLibraryCompose") {
            id = libs.plugins.bookmarker.android.library.compose.get().pluginId
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }
        register("androidMviOrbit") {
            id = libs.plugins.bookmarker.android.mvi.orbit.get().pluginId
            implementationClass = "AndroidMviOrbitPlugin"
        }
        register("androidMviMavericks") {
            id = libs.plugins.bookmarker.android.mvi.mavericks.get().pluginId
            implementationClass = "AndroidMviMavericksPlugin"
        }
    }
}
