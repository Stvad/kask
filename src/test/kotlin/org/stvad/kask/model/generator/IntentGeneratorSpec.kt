package org.stvad.kask.model.generator

import com.squareup.kotlinpoet.ClassName
import io.kotlintest.shouldThrow
import io.kotlintest.specs.WordSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.stvad.kask.CodeGenerationError
import org.stvad.kask.model.IntentDefinition
import org.stvad.kask.model.generator.utils.createDummySlot

class IntentGeneratorSpec : WordSpec({
    "IntentGenerator" should {
        val dummySlot = createDummySlot()

        "throw validation exception if non-alphanumeric name is provided as intent name" {
            shouldThrow<CodeGenerationError> {
                IntentGenerator(IntentDefinition("test.Int<ent>", slotDefinitions = listOf(dummySlot))).generate()
            }
        }

        "call vendor to retrieve slot type if intent has slots" {
            val slotVendor = mockk<SlotVendor>()

            every { slotVendor.classNameForSlot(dummySlot) } returns ClassName("", "dummyName")

            IntentGenerator(
                    IntentDefinition("testIntent", slotDefinitions = listOf(dummySlot)),
                    slotVendor).generate()

            verify { slotVendor.classNameForSlot(dummySlot) }
        }
    }
})