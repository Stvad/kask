package org.stvad.kask.model

import com.amazon.ask.model.SlotConfirmationStatus
import org.joda.time.Period

abstract class Slot<T>(val type: String, val askSlot: com.amazon.ask.model.Slot) {
    val name: String get() = askSlot.name
    val confirmationStatus: SlotConfirmationStatus get() = askSlot.confirmationStatus

    abstract val value: T?
    //TODO(resolutions?)
}

interface SlotCompanion {
    val type: String
}

class DurationSlot(askSlot: com.amazon.ask.model.Slot) : Slot<Period>("AMAZON.DURATION", askSlot) {
    companion object : SlotCompanion {
        override val type = "AMAZON.DURATION"
    }

    override val value: Period?
        get() = askSlot.value?.let { Period.parse(it) }
}