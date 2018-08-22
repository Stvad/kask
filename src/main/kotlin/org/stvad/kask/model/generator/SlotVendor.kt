package org.stvad.kask.model.generator

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import org.stvad.kask.CodeGenerationError
import org.stvad.kask.model.SlotDefinition
import org.stvad.kask.model.supportedAmazonSlots
import kotlin.reflect.KClass

interface SlotVendor {
    fun classNameForSlot(slot: SlotDefinition): ClassName
    val generatedSlots: List<TypeSpec>
}

class PoeticSlotVendor(private val slotGenerator: SlotGenerator,
                       private val overrideMap: Map<String, KClass<out Any>> = emptyMap()) : SlotVendor {


    override fun classNameForSlot(slotDefinition: SlotDefinition): ClassName {
        return overrideMap.getOrElse(slotDefinition.type) { supportedAmazonSlots[slotDefinition.type] }?.asClassName()
                ?: generateSlot(slotDefinition)
    }

    override val generatedSlots = mutableListOf<TypeSpec>()

    private fun generateSlot(slotDefinition: SlotDefinition): ClassName {
        val generatedSpec = slotGenerator.generate(slotDefinition)
        generatedSlots.add(generatedSpec)
        return ClassName("", generatedSpec.name ?: throw CodeGenerationError("Slot should have a name"))
    }
}