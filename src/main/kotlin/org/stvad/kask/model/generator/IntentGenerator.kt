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
import org.stvad.kask.model.BuiltInIntent
import org.stvad.kask.model.Intent
import org.stvad.kask.model.IntentCompanion
import org.stvad.kask.model.IntentDefinition
import org.stvad.kask.model.SlotDefinition
import org.stvad.kask.model.supportedAmazonSlots
import org.stvad.kask.requireSlot
import org.stvad.verse.toInvocation
import org.stvad.verse.toProperties
import com.amazon.ask.model.Intent as ASKIntent

const val amazonPrefix = "AMAZON."

//todo consider generating tests for generated code :p?
//TODO allow passing custom mapping for the slot types to allow custom slot support and override of defaults
// also consider extracting all things slot related into separate class/set of functions
// poet extensions - verses
class IntentGenerator(private val intentDefinition: IntentDefinition,
                      private val prefixesToRemove: List<String> = listOf(amazonPrefix)) {

    companion object {
        val requiredImports = listOf("org.stvad.kask" to ASKIntent::requireSlot.name)
    }


    fun String.removePrefixes(prefixes: List<String>) =
            prefixes.find { this.startsWith(it) }?.let { this.removePrefix(it) } ?: this

    private val className = ClassName("", intentDefinition.name.removePrefixes(prefixesToRemove))

    //todo make dataclass
    fun generate() =
            classBuilder(className)
//                    .addModifiers(KModifier.DATA) //can't have non val/var params
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
                ParameterSpec.builder(it.name, getSlotClass(it)).build()
            }

    private fun getSlotClass(slotDefinition: SlotDefinition) = supportedAmazonSlots[slotDefinition.type]
            ?: TODO("make this a call to slot generator, ")


    private fun superclassParameters() = listOf(askIntentParameter)

    private fun companion(): TypeSpec = companionObjectBuilder()
            .addSuperinterface(companionInterface)
            .addProperty(companionNameProperty)
            .addFunction(companionIntentIntentInitializer)
            .build()

    private val askIntentParameter = ParameterSpec.builder(Intent::askIntent.name, ASKIntent::class).build()

    private fun slotInitializerInvocation(slotDefinition: SlotDefinition) =
            CodeBlock.of("%T(${askIntentParameter.name}.${ASKIntent::requireSlot.name}(%S))",
                    getSlotClass(slotDefinition),
                    slotDefinition.name)

    private fun slotInitializers() =
            intentDefinition.slotDefinitions
                    .map(this::slotInitializerInvocation)
                    .fold(CodeBlock.of("")) { acc, block -> acc.toBuilder().add(", ").add(block).build() }

    //TODO(this is still kind of bad :()
    private val intentInitializerCode =
            CodeBlock.builder()
                    .add("return %T(", className)
                    .add(askIntentParameter.name)
                    .add(slotInitializers())
                    .add(")")
                    .build()

    private val companionIntentIntentInitializer = FunSpec.builder(IntentCompanion<Any>::fromAskIntent.name)
            .addParameter(askIntentParameter)
            .addModifiers(KModifier.OVERRIDE)
            .addCode(intentInitializerCode)
            .returns(className)
            .build()

    private val companionNameProperty =
            PropertySpec.builder(IntentCompanion<Any>::name.name, String::class, KModifier.OVERRIDE)
                    .initializer("%S", intentDefinition.name)
                    .build()

    private val companionInterface = IntentCompanion::class.asClassName()
            .parameterizedBy(className)
}