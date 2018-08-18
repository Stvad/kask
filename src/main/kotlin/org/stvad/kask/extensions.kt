package org.stvad.kask

import com.amazon.ask.model.Intent

fun Intent.requireSlot(slotName: Any) = slots[slotName.toString()]
        ?: throw SlotMissingException("The slot $slotName is missing. Please check validity of your model.")