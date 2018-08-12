package org.stvad.kask

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.handler.RequestHandler
import com.amazon.ask.model.DialogState
import com.amazon.ask.model.Intent
import com.amazon.ask.model.IntentRequest
import com.amazon.ask.model.Request
import com.amazon.ask.model.Response
import com.amazon.ask.request.Predicates
import java.util.*
import java.util.Optional.empty
import java.util.function.Predicate
import kotlin.reflect.KClass

interface NullSafeRequestHandler : RequestHandler {
    fun handleSafely(input: HandlerInput): Optional<Response>
    fun canHandleSafely(input: HandlerInput): Boolean

    override fun handle(input: HandlerInput?) = if (input != null) handleSafely(input) else empty()
    override fun canHandle(input: HandlerInput?) = if (input != null) canHandleSafely(input) else false
}

abstract class LambdaRequestHandler(private val handler: (HandlerInput) -> Optional<Response>) : NullSafeRequestHandler {
    override fun handleSafely(input: HandlerInput) = handler(input)
}

abstract class IntentRequestHandler(private vararg val intents: String) : NullSafeRequestHandler {
    override fun canHandleSafely(input: HandlerInput) = canHandleIntents(input, intents)

    val HandlerInput.intentRequest get() = (requestEnvelope.request as IntentRequest)

    fun HandlerInput.delegateDialog(): Optional<Response> = responseBuilder.addDelegateDirective(intent).build()

    val HandlerInput.dialogState: DialogState
        get() = intentRequest.dialogState

    val DialogState.isCompleted get() = this == DialogState.COMPLETED

    val HandlerInput.intent: Intent
        get() = intentRequest.intent

    fun Intent.requireSlot(slotName: Any) = slots[slotName.toString()]
            ?: throw SlotMissingException("The slot $slotName is missing. Please check validity of your model.")
}

fun handle(vararg intents: String, handler: (HandlerInput) -> Optional<Response>) = object : LambdaRequestHandler(handler) {
    override fun canHandleSafely(input: HandlerInput) = canHandleIntents(input, intents)
}

fun canHandleIntents(input: HandlerInput, intents: Array<out String>) =
        input.matches(intents.map(Predicates::intentName).reduce(Predicate<HandlerInput>::or))

fun <T : Request> handle(vararg requestTypes: KClass<T>, handler: (HandlerInput) -> Optional<Response>) = object : LambdaRequestHandler(handler) {
    override fun canHandleSafely(input: HandlerInput) =
            input.matches(requestTypes
                    .map(KClass<T>::java)
                    .map(Predicates::requestType)
                    .reduce(Predicate<HandlerInput>::or))
}

