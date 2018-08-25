package org.stvad.kask.model.generator

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import io.kotlintest.specs.WordSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.spyk
import io.mockk.verify
import org.stvad.kask.model.IntentDefinition

class AlexaModelGeneratorSpec : WordSpec({
    "AlexaModelGenerator" should {
        val builderSpy = spyk(FileSpec.builder("", ""))
        val subject = AlexaModelGenerator(listOf(IntentDefinition("simpleIntent")), "", modelSpecBuilder = builderSpy)

        "add one type for intent with no custom slots" {
            subject.generate()
            verify(exactly = 1) { builderSpy.addType(any()) }
        }

        "gracefully handle error in intent generation" {
            mockkConstructor(IntentGenerator::class)
            every { anyConstructed<IntentGenerator>().generate() } throws IllegalStateException()

            subject.generate()

            verify(exactly = 0) { builderSpy.addType(any()) }
        }

        "adds generated slots to the spec" {
            val dummyTypeSpec = TypeSpec.classBuilder("dummyType").build()

            val slotVendor = mockk<SlotVendor>()
            every { slotVendor.generatedSlots } returns setOf(dummyTypeSpec)

            AlexaModelGenerator(emptyList(), "", modelSpecBuilder = builderSpy, slotVendor = slotVendor).generate()

            verify { builderSpy.addType(dummyTypeSpec) }
        }
    }
}) {
    override fun isInstancePerTest() = true
}