package org.stvad.kask.model

import com.amazon.ask.model.Slot
import com.github.debop.kodatimes.months
import com.github.debop.kodatimes.toLocalTime
import io.kotlintest.forAll
import io.kotlintest.matchers.collections.shouldHaveSize
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import org.stvad.kask.model.Slot as KaskSlot


class SlotSpec : WordSpec({
    val emptySlot = Slot.builder().build()

    "AllSlots" should {
        "handle null values in askSlot.value" {
            val supportedSlotTypes =
                    listOf(::NumberSlot,
                            ::FourDigitNumberSlot,
                            ::DurationSlot,
                            ::TimeSlot,
                            ::PhoneNumberSlot,
                            ::StringSlot)

            supportedSlotTypes.shouldHaveSize(supportedAmazonSlots.size + 1)

            forAll(supportedSlotTypes) { SlotConstructor ->
                SlotConstructor.call(emptySlot).value shouldBe null
            }
        }
    }

    "DurationSlot" should {
        "parse the duration value when it's present" {
            val oneMonthSlot = Slot.builder().withValue(1.months().toString()).build()

            DurationSlot(oneMonthSlot).value shouldBe 1.months()
        }
    }

    "NumberSlot" should {
        "parse Long value from the slot" {
            val longSlot = Slot.builder().withValue(Long.MAX_VALUE.toString()).build()

            NumberSlot(longSlot).value shouldBe Long.MAX_VALUE
        }
    }

    "TimeSlot" should {
        "be able to parse ISO date" {
            val testTime = "12:00"
            val middaySlot = Slot.builder().withValue(testTime).build()

            TimeSlot(middaySlot).value shouldBe testTime.toLocalTime()
        }

        "be able to interpret the Fuzzy Amazon time" {
            val timeOfDay = "MO"
            val morningSlot = Slot.builder().withValue(timeOfDay).build()

            TimeSlot(morningSlot).value shouldBe TimeSlot.defaultFuzzyTimeMap[timeOfDay]?.toLocalTime()
        }
    }
})
