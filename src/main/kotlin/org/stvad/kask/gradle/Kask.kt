package org.stvad.kask.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.stvad.kask.model.generator.KaskGenerator
import javax.inject.Inject

open class Kask @Inject constructor() : DefaultTask() {

    var packageName = ""

    @InputFile
    val modelPath: RegularFileProperty = newInputFile()

    @OutputDirectory
    val outputDirectory: DirectoryProperty = newOutputDirectory()

    @TaskAction
    fun generate() {
        KaskGenerator.generateAlexaModel(packageName, modelPath.asFile.get(), outputDirectory.asFile.get())
    }

}