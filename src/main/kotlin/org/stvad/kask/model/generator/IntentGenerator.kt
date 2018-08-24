package org.stvad.kask.model.generator

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec.Companion.constructorBuilder
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeSpec.Companion.classBuilder
import org.stvad.kask.CodeGenerationError
import org.stvad.kask.isAlphaNum
import org.stvad.kask.model.BuiltInIntent
import org.stvad.kask.model.Intent
import org.stvad.kask.model.IntentDefinition
import org.stvad.kask.model.amazonPrefix
import org.stvad.kask.removeFartsFoundPrefix
import org.stvad.kask.requireSlot
import org.stvad.verse.toInvocation
import org.stvad.verse.toProperties
import com.amazon.ask.model.Intent as ASKIntent

class IntentGenerator(private val intentDefinition: IntentDefinition,
                      private val slotVendor: SlotVendor = PoeticSlotVendor(),
                      private val prefixesToRemove: List<String> = listOf(amazonPrefix)) {
    companion object {
        val requiredImports = listOf("org.stvad.kask" to ASKIntent::requireSlot.name)
    }

    private fun className(): ClassName {
        val name = intentDefinition.name.removeFartsFoundPrefix(prefixesToRemove)
        if (!name.isAlphaNum()) throw CodeGenerationError("$name is not a valid intent name")

        return ClassName("", name)
    }

    fun generate() =
            classBuilder(className())
                    .primaryConstructor(constructor())
                    .addProperties(slotParameters().toProperties())
                    .superclass(superclass())
                    .addSuperclassConstructorParameter(superclassParameters().toInvocation())
                    .addType(IntentCompanionGenerator(intentDefinition, className(), slotVendor).generate())
                    .build()

    private fun superclass() =
            if (intentDefinition.name.startsWith(amazonPrefix)) BuiltInIntent::class
            else Intent::class


    private fun constructor() = constructorBuilder()
            .addParameters(superclassParameters())
            .addParameters(slotParameters())
            .build()

    //todo consider camelcase for names
    private fun slotParameters() =
            intentDefinition.slotDefinitions.map {
                ParameterSpec.builder(it.name, slotVendor.classNameForSlot(it)).build()
            }

    private fun superclassParameters() = listOf(askIntentParameter)
}