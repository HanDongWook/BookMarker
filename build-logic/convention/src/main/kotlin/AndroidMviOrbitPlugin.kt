import com.oliveyoung.global.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidMviOrbitPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            dependencies {
                "implementation"(libs.findLibrary("orbit.core").get())
                "implementation"(libs.findLibrary("orbit.viewmodel").get())
                "implementation"(libs.findLibrary("orbit.compose").get())
            }
        }
    }
}
