package org.stvad.kask.model.generator

import com.squareup.kotlinpoet.TypeSpec
import org.stvad.kask.model.SlotDefinition

interface SlotGenerator {
    fun generate(slotDefinition: SlotDefinition): TypeSpec
}