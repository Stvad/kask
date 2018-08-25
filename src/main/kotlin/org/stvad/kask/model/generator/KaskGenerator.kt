package org.stvad.kask.model.generator

import com.squareup.moshi.Moshi
import org.stvad.kask.InvalidModelException
import org.stvad.kask.model.IntentDefinition
import org.stvad.kask.model.InteractionModelEnvelope
import org.stvad.kask.model.jsonAdapter
import java.io.File
import kotlin.reflect.KClass

object KaskGenerator {
    fun generateAlexaModel(packageName: String,
                           modelPath: File,
                           outputPath: File,
                           slotOverrideMap: Map<String, KClass<out Any>> = emptyMap()) {
        val intentDefinitions = getIntentDefinitions(modelPath)

        AlexaModelGenerator(intentDefinitions, packageName, slotOverrideMap)
                .generate().writeTo(outputPath)
    }

    private fun getIntentDefinitions(modelPath: File): List<IntentDefinition> {
        val interactionModelEnvelope =
                InteractionModelEnvelope.jsonAdapter(Moshi.Builder().build()).fromJson(modelPath.readText())
                        ?: throw InvalidModelException("The supplied alexa language model is not valid")

        return interactionModelEnvelope.interactionModel.languageModel.intentDefinitions
    }
}