import com.google.devtools.ksp.gradle.KspExtension
import com.oliveyoung.global.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "bookmarker.android.library")
            apply(plugin = "bookmarker.hilt")

            dependencies {
                "implementation"(project(":core:ui"))
                "implementation"(project(":core:designsystem"))
                "debugImplementation"(libs.findLibrary("showkase").get())
                "implementation"(libs.findLibrary("showkase.annotation").get())
                "kspDebug"(libs.findLibrary("showkase.processor").get())
            }

            pluginManager.withPlugin("com.google.devtools.ksp") {
                extensions.configure<KspExtension> {
                    arg("skipPrivatePreviews", "true")
                }
            }
        }
    }
}
