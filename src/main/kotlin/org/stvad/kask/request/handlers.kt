package org.stvad.kask.request

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.handler.RequestHandler
import com.amazon.ask.model.Request
import com.amazon.ask.model.Response
import com.amazon.ask.request.Predicates
import org.stvad.kask.model.IntentCompanion
import java.util.Optional
import java.util.function.Predicate
import kotlin.reflect.KClass

abstract class LambdaRequestHandler(private val handler: (HandlerInput) -> Optional<Response>) : RequestHandler {
    override fun handle(input: HandlerInput) = handler(input)
}

fun handle(vararg intents: String, handler: (HandlerInput) -> Optional<Response>) = object : LambdaRequestHandler(handler) {
    override fun canHandle(input: HandlerInput) = canHandleIntents(input, intents)
}

fun handle(vararg intents: IntentCompanion<out Any>, handler: (HandlerInput) -> Optional<Response>) = object : LambdaRequestHandler(handler) {
    override fun canHandle(input: HandlerInput) = canHandleIntents(input, intents.map { it.name }.toTypedArray())
}

fun canHandleIntents(input: HandlerInput, intents: Array<out String>) =
        input.matches(intents.map(Predicates::intentName).reduce(Predicate<HandlerInput>::or))

fun <T : Request> handle(vararg requestTypes: KClass<T>, handler: (HandlerInput) -> Optional<Response>) = object : LambdaRequestHandler(handler) {
    override fun canHandle(input: HandlerInput) =
            input.matches(requestTypes
                    .map(KClass<T>::java)
                    .map(Predicates::requestType)
                    .reduce(Predicate<HandlerInput>::or))
}

