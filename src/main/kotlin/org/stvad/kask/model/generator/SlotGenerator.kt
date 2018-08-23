package org.stvad.kask.model.generator

import com.squareup.kotlinpoet.FunSpec.Companion.constructorBuilder
import com.squareup.kotlinpoet.TypeSpec
import org.stvad.kask.model.SlotDefinition
import org.stvad.kask.model.StringSlot
import org.stvad.kask.model.amazonPrefix
import org.stvad.kask.removeFartsFoundPrefix
import org.stvad.verse.toInvocation

interface SlotGenerator {
    fun generate(slotDefinition: SlotDefinition): TypeSpec
}

class PoeticSlotGenerator(private val prefixesToRemove: List<String> = listOf(amazonPrefix)) : SlotGenerator {
    override fun generate(slotDefinition: SlotDefinition) =
            TypeSpec.classBuilder(classNameFor(slotDefinition))
                    .superclass(StringSlot::class)
                    .addSuperclassConstructorParameter(superclassParameters().toInvocation())
                    .primaryConstructor(constructor())
                    .build()

    private fun classNameFor(slotDefinition: SlotDefinition) = slotDefinition.name.removeFartsFoundPrefix(prefixesToRemove)
    private fun constructor() = constructorBuilder().addParameters(superclassParameters()).build()

    private fun superclassParameters() = listOf(askSlotParameter)
}