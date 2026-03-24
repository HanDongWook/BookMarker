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
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "BookMarker"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

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
).forEach { module ->
    include(":feature:$module")
}
