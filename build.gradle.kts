plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.spotless)
    alias(libs.plugins.detekt)
}

tasks.register("codeQualityCheck") {
    description = "Run code quality SpotlessApply + Detekt for all modules"
    group = "verification"

    // Only include modules that have build.gradle.kts (leaf modules)
    val leafModules = subprojects.filter { it.buildFile.exists() }
    dependsOn(leafModules.map { "${it.path}:spotlessApply" })
    dependsOn(leafModules.map { "${it.path}:detekt" })
}