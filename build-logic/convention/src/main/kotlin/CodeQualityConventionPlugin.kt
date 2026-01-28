import com.diffplug.gradle.spotless.SpotlessExtension
import com.oliveyoung.global.libs
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType

class CodeQualityConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val ktlintVersion = libs.findVersion("ktlint").get().toString()

            pluginManager.apply("com.diffplug.spotless")
            pluginManager.apply("io.gitlab.arturbosch.detekt")

            extensions.configure<SpotlessExtension> {
                kotlin {
                    target("**/*.kt")
                    targetExclude("**/build/**/*.kt")
                    ktlint(ktlintVersion)
                        .editorConfigOverride(
                            mapOf(
                                "android" to "true",
                                "ktlint_standard_backing-property-naming" to "disabled",
                                "ktlint_standard_no-unused-imports" to "enabled",
                                "max_line_length" to "120",
                                "indent_size" to "4",
                            )
                        )
                    trimTrailingWhitespace()
                    endWithNewline()
                }

                kotlinGradle {
                    target("**/*.gradle.kts")
                    ktlint()
                    trimTrailingWhitespace()
                    endWithNewline()
                }

                format("xml") {
                    target("**/*.xml")
                    trimTrailingWhitespace()
                    endWithNewline()
                }
            }

            extensions.configure<DetektExtension> {
                buildUponDefaultConfig = true
                allRules = false
                config.setFrom(file("${rootDir}/config/detekt/detekt.yml"))
            }

            tasks.withType<Detekt>().configureEach {
                reports {
                    html.required.set(true)
                    xml.required.set(true)
                    txt.required.set(false)
                    md.required.set(false)
                    sarif.required.set(false)
                }
            }
        }
    }
}
