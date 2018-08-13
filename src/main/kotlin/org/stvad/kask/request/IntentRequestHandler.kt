package org.stvad.kask.request

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.model.Response
import org.stvad.kask.model.Intent
import org.stvad.kask.model.IntentCompanion
import java.util.Optional

abstract class IntentRequestHandler<T : Intent>(protected val intentCompanion: IntentCompanion<T>) : BasicIntentRequestHandler(intentCompanion.name) {
    override fun handleSafely(input: HandlerInput) = handleSafely(input, intentCompanion.fromAskIntent(input.intent))
    abstract fun handleSafely(input: HandlerInput, intent: T): Optional<Response>
}