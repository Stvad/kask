package org.stvad.kask

import com.amazon.ask.model.Intent
import com.amazon.ask.model.Slot
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.WordSpec

class ExtensionsSpec : WordSpec({
    "requireSlot" should {
        val testSlot = Slot.builder().withName("test_slot_name").build()
        val otherSlotName = "other_slot_name"

        "throw if slot is missing" {
            val testIntent = Intent.builder().putSlotsItem(otherSlotName, testSlot).build()
            shouldThrow<SlotMissingException> {
                testIntent.requireSlot(testSlot.name)
            }
        }

        "return slot if present" {
            val testIntent = Intent.builder().putSlotsItem(testSlot.name, testSlot).build()
            testIntent.requireSlot(testSlot.name) shouldBe testSlot
        }
    }
})