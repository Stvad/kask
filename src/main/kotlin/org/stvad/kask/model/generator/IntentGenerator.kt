package org.stvad.kask.model.generator

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.FunSpec.Companion.constructorBuilder
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.TypeSpec.Companion.classBuilder
import com.squareup.kotlinpoet.TypeSpec.Companion.companionObjectBuilder
import com.squareup.kotlinpoet.asClassName
import org.stvad.kask.CodeGenerationError
import org.stvad.kask.isAlphaNum
import org.stvad.kask.model.BuiltInIntent
import org.stvad.kask.model.Intent
import org.stvad.kask.model.IntentCompanion
import org.stvad.kask.model.IntentDefinition
import org.stvad.kask.model.SlotDefinition
import org.stvad.kask.model.amazonPrefix
import org.stvad.kask.removeFartsFoundPrefix
import org.stvad.kask.requireSlot
import org.stvad.verse.toInvocation
import org.stvad.verse.toProperties
import com.amazon.ask.model.Intent as ASKIntent

// todo consider extracting all things slot related into separate class/set of functions
class IntentGenerator(private val intentDefinition: IntentDefinition,
                      private val slotVendor: SlotVendor = PoeticSlotVendor(PoeticSlotGenerator()),
                      private val prefixesToRemove: List<String> = listOf(amazonPrefix)) {

    companion object {
        val requiredImports = listOf("org.stvad.kask" to ASKIntent::requireSlot.name)
    }

    private fun className(): ClassName {
        val name = intentDefinition.name.removeFartsFoundPrefix(prefixesToRemove)
        if (!name.isAlphaNum()) throw CodeGenerationError("$name is not a valid intent name")

        return ClassName("", name)
    }

    //todo make dataclass
    fun generate() =
            classBuilder(className())
                    .primaryConstructor(constructor())
                    .addProperties(slotParameters().toProperties())
                    .superclass(superclass())
                    .addSuperclassConstructorParameter(superclassParameters().toInvocation())
                    .addType(companion())
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

    private fun companion(): TypeSpec = companionObjectBuilder()
            .addSuperinterface(companionInterface)
            .addProperty(companionNameProperty)
            .addFunction(companionIntentIntentInitializer)
            .build()

    private fun slotInitializerInvocation(slotDefinition: SlotDefinition) =
            CodeBlock.of("%T(${askIntentParameter.name}.${ASKIntent::requireSlot.name}(%S))",
                    slotVendor.classNameForSlot(slotDefinition),
                    slotDefinition.name)

    private fun slotInitializers() =
            intentDefinition.slotDefinitions
                    .map(this::slotInitializerInvocation)
                    .fold(CodeBlock.of("")) { acc, block -> acc.toBuilder().add(", ").add(block).build() }

    private val intentInitializerCode =
            CodeBlock.builder()
                    .add("return %T(", className())
                    .add(askIntentParameter.name)
                    .add(slotInitializers())
                    .add(")")
                    .build()

    private val companionIntentIntentInitializer = FunSpec.builder(IntentCompanion<Any>::fromAskIntent.name)
            .addParameter(askIntentParameter)
            .addModifiers(KModifier.OVERRIDE)
            .addCode(intentInitializerCode)
            .returns(className())
            .build()

    private val companionNameProperty =
            PropertySpec.builder(IntentCompanion<Any>::name.name, String::class, KModifier.OVERRIDE)
                    .initializer("%S", intentDefinition.name)
                    .build()

    private val companionInterface = IntentCompanion::class.asClassName()
            .parameterizedBy(className())
}