package org.stvad.kask.model.generator

import com.amazon.ask.model.Intent
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import org.stvad.kask.model.IntentCompanion
import org.stvad.kask.model.IntentDefinition
import org.stvad.kask.model.SlotDefinition
import org.stvad.kask.requireSlot

class IntentCompanionGenerator(private val intentDefinition: IntentDefinition,
                               private val intentClassName: ClassName,
                               private val slotVendor: SlotVendor) {

    fun generate(): TypeSpec = TypeSpec.companionObjectBuilder()
            .addSuperinterface(companionInterface)
            .addProperty(companionNameProperty)
            .addFunction(companionIntentIntentInitializer)
            .build()

    private val companionInterface = IntentCompanion::class.asClassName()
            .parameterizedBy(intentClassName)

    private val companionNameProperty =
            PropertySpec.builder(IntentCompanion<Any>::name.name, String::class, KModifier.OVERRIDE)
                    .initializer("%S", intentDefinition.name)
                    .build()

    private val intentInitializerCode =
            CodeBlock.builder()
                    .add("return %T(", intentClassName)
                    .add(askIntentParameter.name)
                    .add(slotInitializers())
                    .add(")")
                    .build()

    private val companionIntentIntentInitializer = FunSpec.builder(IntentCompanion<Any>::fromAskIntent.name)
            .addParameter(askIntentParameter)
            .addModifiers(KModifier.OVERRIDE)
            .addCode(intentInitializerCode)
            .returns(intentClassName)
            .build()

    private fun slotInitializerInvocation(slotDefinition: SlotDefinition) =
            CodeBlock.of("%T(${askIntentParameter.name}.${Intent::requireSlot.name}(%S))",
                    slotVendor.classNameForSlot(slotDefinition),
                    slotDefinition.name)

    private fun slotInitializers() =
            intentDefinition.slotDefinitions
                    .map(this::slotInitializerInvocation)
                    .fold(CodeBlock.of("")) { acc, block -> acc.toBuilder().add(", ").add(block).build() }
}