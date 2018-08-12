package org.stvad.kask.request

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.handler.RequestHandler
import com.amazon.ask.model.Request
import com.amazon.ask.model.Response
import com.amazon.ask.request.Predicates
import java.util.Optional
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

