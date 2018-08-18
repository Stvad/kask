package org.stvad.kask.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.provider.Provider


class KaskGeneratorGradlePlugin : Plugin<Project> {
    companion object {
        val Project.generatedOutput: Provider<Directory>
            get() = layout.buildDirectory.dir("generated")
                    .map { it.dir("source") }
                    .map { it.dir("kask") }
                    .map { it.dir("main") }
    }

    override fun apply(target: Project?) {
        target ?: throw IllegalStateException("Project is not supposed to be null")

        val kask = target.tasks.create("kask", Kask::class.java) {
            it.outputDirectory.set(target.generatedOutput)
        }

        registerSources(target, kask)
    }

    private fun registerSources(project: Project, kask: Kask?) {
        project.plugins.apply(JavaPlugin::class.java)

        val javaPlugin = project.convention.getPlugin(JavaPluginConvention::class.java)
        javaPlugin.sourceSets.all {
            it.output.dir(mapOf("builtBy" to kask), project.generatedOutput)
            it.java.srcDir(project.generatedOutput)
        }
    }
}