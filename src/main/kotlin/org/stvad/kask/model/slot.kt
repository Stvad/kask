package org.stvad.kask.model

import com.amazon.ask.model.SlotConfirmationStatus
import com.github.debop.kodatimes.toLocalTime
import org.joda.time.LocalTime
import org.joda.time.Period

abstract class Slot<T>(val askSlot: com.amazon.ask.model.Slot) {
    val name: String get() = askSlot.name
    val confirmationStatus: SlotConfirmationStatus get() = askSlot.confirmationStatus
    val stringValue: String? get() = askSlot.value

    abstract val value: T?
    //TODO(resolutions?)
}

interface SlotCompanion {
    val type: String
}

class PhoneNumber(private val number: String) : CharSequence by number

/**
 * AMAZON.DATE - is non standard and needs a custom code to parse it (https://developer.amazon.com/docs/custom-skills/slot-type-reference.html#date).
 * As I didn't see anything that does that for Java/Kotlin atm - not implementing it for now.
 *
 * The Phrase slot types (AMAZON.SearchQuery & List Slot types) are not implemented explicitly, but the intention is to
 * generate them as needed based on StringSlot.
 */
val supportedAmazonSlots = listOf(DurationSlot::class, NumberSlot::class, FourDigitNumberSlot::class)

/**
 * https://developer.amazon.com/docs/custom-skills/slot-type-reference.html#duration
 */
class DurationSlot(askSlot: com.amazon.ask.model.Slot) : Slot<Period>(askSlot) {
    companion object : SlotCompanion {
        override val type = "AMAZON.DURATION"
    }

    override val value: Period?
        get() = stringValue?.let { Period.parse(it) }
}

/**
 * https://developer.amazon.com/docs/custom-skills/slot-type-reference.html#time
 */
class TimeSlot(askSlot: com.amazon.ask.model.Slot,
               val fuzzyTimeMap: Map<String, String> = defaultFuzzyTimeMap) : Slot<LocalTime>(askSlot) {
    companion object : SlotCompanion {
        override val type = "AMAZON.TIME"

        val defaultFuzzyTimeMap = mapOf(
                "NI" to "21:00",
                "MO" to "09:00",
                "AF" to "12:00",
                "EV" to "18:00"
        )
    }

    override val value: LocalTime?
        get() = fuzzyTimeMap.getOrDefault(stringValue, stringValue)?.toLocalTime()
}

/**
 * https://developer.amazon.com/docs/custom-skills/slot-type-reference.html#number
 */
open class NumberSlot(askSlot: com.amazon.ask.model.Slot) : Slot<Long>(askSlot) {
    companion object : SlotCompanion {
        override val type = "AMAZON.NUMBER"
    }

    override val value get() = stringValue?.toLong()
}

/**
 * https://developer.amazon.com/docs/custom-skills/slot-type-reference.html#four_digit_number
 */
class FourDigitNumberSlot(askSlot: com.amazon.ask.model.Slot) : NumberSlot(askSlot) {
    companion object : SlotCompanion {
        override val type = "AMAZON.FOUR_DIGIT_NUMBER"
    }
}

