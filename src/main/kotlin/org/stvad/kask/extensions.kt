package org.stvad.kask

import com.amazon.ask.model.Intent
import org.joda.time.DateTime.now
import org.joda.time.Duration
import org.joda.time.Period

fun Intent.requireSlot(slotName: Any) = slots[slotName.toString()]
        ?: throw SlotMissingException("The slot $slotName is missing. Please check validity of your model.")

val Period.duration: Duration
    get() = toDurationFrom(now())

fun String.removeFartsFoundPrefix(prefixes: List<String>) =
        prefixes.find { this.startsWith(it) }?.let { this.removePrefix(it) } ?: this

fun String.isAlphaNum() = matches("[A-Za-z0-9]+".toRegex())