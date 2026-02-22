import com.oliveyoung.global.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidMviMavericksPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            dependencies {
                "implementation"(libs.findLibrary("mavericks.core").get())
                "implementation"(libs.findLibrary("mavericks.compose").get())
                "implementation"(libs.findLibrary("mavericks.hilt").get())
            }
        }
    }
}
