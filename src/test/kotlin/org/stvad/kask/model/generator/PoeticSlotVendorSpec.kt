package org.stvad.kask.model.generator

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import io.kotlintest.matchers.collections.shouldContain
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.stvad.kask.model.DurationSlot
import org.stvad.kask.model.generator.utils.createDummySlot

class PoeticSlotVendorSpec : WordSpec({
    "PoeticSlotVendor" should {
        val slotGenerator = mockk<SlotGenerator>()

        "return a ClassName for the slot that is directly supported" {
            PoeticSlotVendor(slotGenerator).classNameForSlot(createDummySlot(type = DurationSlot.type)) shouldBe DurationSlot::class.asClassName()
        }

        "return a ClassName for the slot that is provided by an override" {
            val overriddenSlot = createDummySlot(type = "overriddenType")
            val slotVendor = PoeticSlotVendor(slotGenerator, mapOf(overriddenSlot.type to PoeticSlotVendorSpec::class))

            slotVendor.classNameForSlot(overriddenSlot) shouldBe PoeticSlotVendorSpec::class.asClassName()
        }

        "if slot is not in default supported list - generate a slot type based on String slot type and return ClassName for it" {
            val unsupportedSlot = createDummySlot(type = "unsupported_slot")
            val unsupportedClassName = ClassName("", unsupportedSlot.type)
            val generatedSpec = TypeSpec.classBuilder(unsupportedClassName).build()

            every { slotGenerator.generate(unsupportedSlot) } returns generatedSpec

            val slotVendor = PoeticSlotVendor(slotGenerator)
            slotVendor.classNameForSlot(unsupportedSlot) shouldBe unsupportedClassName

            verify { slotGenerator.generate(unsupportedSlot) }
            slotVendor.generatedSlots.shouldContain(generatedSpec)
        }
    }
})

