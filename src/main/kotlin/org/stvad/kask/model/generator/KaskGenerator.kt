package org.stvad.kask.model.generator

import com.squareup.kotlinpoet.FileSpec
import com.squareup.moshi.Moshi
import org.stvad.kask.InvalidModelException
import org.stvad.kask.model.InteractionModelEnvelope
import org.stvad.kask.model.jsonAdapter
import java.io.File

object KaskGenerator {

    private const val fileName = "intents"

    fun generateAlexaModel(packageName: String, modelPath: File, outputPath: File) {
        val interactionModelEnvelope =
                InteractionModelEnvelope.jsonAdapter(Moshi.Builder().build()).fromJson(modelPath.readText())
                        ?: throw InvalidModelException("The supplied alexa language model is not valid")

        val intentDefinitions = interactionModelEnvelope.interactionModel.languageModel.intentDefinitions

        val intents = FileSpec.builder(packageName, fileName).apply {
            intentDefinitions.map { IntentGenerator(it).generate() }.forEach { addType(it) }
        }
        IntentGenerator.requiredImports.forEach { intents.addImport(it.first, it.second) }

        intents.build().writeTo(outputPath)
        //todo add comment about code being generated
    }
}