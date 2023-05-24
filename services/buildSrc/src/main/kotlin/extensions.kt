import org.gradle.plugin.use.PluginDependenciesSpec
import org.gradle.plugin.use.PluginDependencySpec

fun PluginDependenciesSpec.standardKotlinJvmModule(): PluginDependencySpec {
    return id("co.vgw.example.standard-kotlin-jvm-module")
}
