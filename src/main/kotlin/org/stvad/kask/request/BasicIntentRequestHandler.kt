package org.stvad.kask.request

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.handler.RequestHandler
import com.amazon.ask.model.DialogState
import com.amazon.ask.model.Intent
import com.amazon.ask.model.IntentRequest
import com.amazon.ask.model.Response
import java.util.Optional

abstract class BasicIntentRequestHandler(private vararg val intents: String) : RequestHandler {
    override fun canHandle(input: HandlerInput) = canHandleIntents(input, intents)

    val HandlerInput.intentRequest get() = (requestEnvelope.request as IntentRequest)

    fun HandlerInput.delegateDialog(): Optional<Response> = responseBuilder.addDelegateDirective(intent).build()

    val HandlerInput.dialogState: DialogState
        get() = intentRequest.dialogState

    val DialogState.isCompleted get() = this == DialogState.COMPLETED

    val HandlerInput.intent: Intent
        get() = intentRequest.intent
}