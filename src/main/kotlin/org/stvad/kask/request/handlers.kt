package org.stvad.kask.request

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.handler.RequestHandler
import com.amazon.ask.model.Request
import com.amazon.ask.model.Response
import com.amazon.ask.request.Predicates
import com.amazon.ask.request.Predicates.requestType
import com.amazon.ask.response.ResponseBuilder
import org.stvad.kask.model.IntentCompanion
import java.util.Optional
import java.util.function.Predicate
import kotlin.reflect.KClass

typealias ResponseContext = ResponseBuilder.(HandlerInput) -> Unit

abstract class LambdaRequestHandler(private val handler: (HandlerInput) -> Optional<Response>) : RequestHandler {
    override fun handle(input: HandlerInput) = handler(input)
}

fun handle(vararg intents: String, handler: (HandlerInput) -> Optional<Response>) = object : LambdaRequestHandler(handler) {
    override fun canHandle(input: HandlerInput) = canHandleIntents(input, intents)
}

fun handle(handler: (HandlerInput) -> Optional<Response>, vararg intents: String) = handle(*intents, handler = handler)

fun respond(vararg intents: String, responseContext: ResponseContext) =
        handle(*intents) { it.respond(responseContext) }

fun handle(vararg intents: IntentCompanion<out Any>, handler: (HandlerInput) -> Optional<Response>) = object : LambdaRequestHandler(handler) {
    override fun canHandle(input: HandlerInput) = canHandleIntents(input, intents.map { it.name }.toTypedArray())
}

fun handle(handler: (HandlerInput) -> Optional<Response>, vararg intents: IntentCompanion<out Any>) = handle(*intents, handler = handler)

fun respond(vararg intents: IntentCompanion<out Any>, responseContext: ResponseContext) =
        handle(*intents) { it.respond(responseContext) }

fun <T : Request> handle(vararg requestTypes: KClass<out T>, handler: (HandlerInput) -> Optional<Response>) = object : LambdaRequestHandler(handler) {
    override fun canHandle(input: HandlerInput) =
            input.matches(requestTypes
                    .map { requestType(it.java) }
                    .reduce(Predicate<HandlerInput>::or))
}

fun <T : Request> handle(handler: (HandlerInput) -> Optional<Response>, vararg requestTypes: Class<out T>) =
        handle(*requestTypes.map { it.kotlin }.toTypedArray(), handler = handler)

fun <T : Request> respond(vararg requestTypes: KClass<out T>, responseContext: ResponseContext) =
        handle(*requestTypes) { it.respond(responseContext) }

fun canHandleIntents(input: HandlerInput, intents: Array<out String>) =
        input.matches(intents.map(Predicates::intentName).reduce(Predicate<HandlerInput>::or))

fun HandlerInput.respond(responseContext: ResponseContext): Optional<Response> =
        responseBuilder.apply { responseContext(this@respond) }.build()

