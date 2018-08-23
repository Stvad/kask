package org.stvad.kask.model.generator

import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import org.stvad.kask.model.StringSlot
import org.stvad.kask.model.generator.utils.createDummySlot

class PoeticSlotGeneratorSpec : WordSpec({
    "PoeticSlotGenerator" should {
        "return typespec with the name where specified prefix was removed" {
            val prefix = "prefix."
            val suffix = "suffix"
            val slotDefinition = createDummySlot(name = prefix + suffix)

            PoeticSlotGenerator(listOf(prefix)).generate(slotDefinition).name shouldBe suffix
        }

        "generated typespec should have the StringSlot as supertype" {
            println(PoeticSlotGenerator().generate(createDummySlot()).superclass)
            PoeticSlotGenerator().generate(createDummySlot()).superclass.toString() shouldBe StringSlot::class.qualifiedName
        }
    }
})