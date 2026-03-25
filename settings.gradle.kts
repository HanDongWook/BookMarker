pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("com.gradle.develocity") version "4.3.2"
}

develocity {
    buildScan {
        termsOfUseUrl.set("https://gradle.com/help/legal-terms-of-use")
        termsOfUseAgree.set("yes")
        publishing.onlyIf { !System.getenv("CI").isNullOrEmpty() }
        uploadInBackground.set(System.getenv("CI").isNullOrEmpty())
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "BookMarker"
include(":app")

listOf(
    "model",
    "data",
    "datastore",
    "domain",
    "designsystem",
    "navigation",
    "ui",
    "common"
).forEach { module ->
    include(":core:$module")
}

listOf(
    "home",
    "settings",
    "importguide",
    "trends",
).forEach { module ->
    include(":feature:$module")
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
