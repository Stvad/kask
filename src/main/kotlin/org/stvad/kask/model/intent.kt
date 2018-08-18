package org.stvad.kask.model

import com.amazon.ask.model.IntentConfirmationStatus

abstract class Intent(val askIntent: com.amazon.ask.model.Intent) {
    val confirmationStatus: IntentConfirmationStatus get() = askIntent.confirmationStatus
}

abstract class BuiltInIntent(askIntent: com.amazon.ask.model.Intent) : Intent(askIntent)

interface IntentCompanion<T> {
    val name: String
    fun fromAskIntent(askIntent: com.amazon.ask.model.Intent): T
}