package org.stvad.kask.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class InteractionModelEnvelope(val interactionModel: InteractionModel) {
    companion object
}

@JsonClass(generateAdapter = true)
data class InteractionModel(val languageModel: LanguageModel, val prompts: List<Any>)

@JsonClass(generateAdapter = true)
data class Dialog(val intents: List<Any>)

@JsonClass(generateAdapter = true)
data class LanguageModel(val invocationName: String, @Json(name = "intents") val intentDefinitions: List<IntentDefinition>, val types: List<Any>)

@JsonClass(generateAdapter = true)
data class IntentDefinition(val name: String, val samples: List<String>, @Json(name = "slots") val slotDefinitions: List<SlotDefinition> = emptyList())

@JsonClass(generateAdapter = true)
data class SlotDefinition(val name: String, val type: String, val samples: List<String>)