package org.stvad.kask.request

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.handler.RequestHandler
import com.amazon.ask.model.Intent
import com.amazon.ask.model.IntentRequest
import com.amazon.ask.model.LaunchRequest
import com.amazon.ask.model.Request
import com.amazon.ask.model.RequestEnvelope
import io.kotlintest.Matcher
import io.kotlintest.Result
import io.kotlintest.should
import io.kotlintest.specs.WordSpec
import java.util.Optional.empty

fun handlerInputForIntent(intentName: String): HandlerInput =
        handlerInputForRequest(IntentRequest.builder().withIntent(
                Intent.builder().withName(intentName)
                        .build()).build())

fun handlerInputForRequest(request: Request): HandlerInput =
        HandlerInput.builder().withRequestEnvelope(
                RequestEnvelope.builder().withRequest(request).build()
        ).build()

fun handleRequestsWithIntents(vararg intents: String) = object : Matcher<RequestHandler> {
    override fun test(value: RequestHandler) =
            Result(intents.map { value.canHandle(handlerInputForIntent(it)) }.reduce { acc, next -> acc && next },
                    "The $value handler should be able to handle all the intents in ${intents.contentToString()}")
}

fun handleRequestsWithIntents(vararg requestTypes: Request) = object : Matcher<RequestHandler> {
    override fun test(value: RequestHandler) =
            Result(requestTypes.map { value.canHandle(handlerInputForRequest(it)) }.reduce { acc, next -> acc && next },
                    "The $value handler should be able to handle all the request types in ${requestTypes.contentToString()}")
}

class HandlerSpec : WordSpec({
    "Intent based handlers" should {
        "match inputs with appropriate intent" {
            val intents = arrayOf("firstIntent", "secondIntent")
            handle(*intents) { empty() } should handleRequestsWithIntents(*intents)
        }
    }

    "Request type based handlers" should {
        "match inputs with appropriate request type" {
            val requestTypes = arrayOf(IntentRequest::class, LaunchRequest::class)
            val requests = arrayOf(IntentRequest.builder().build(), LaunchRequest.builder().build())
            handle(*requestTypes) { empty() } should handleRequestsWithIntents(*requests)
        }
    }
})