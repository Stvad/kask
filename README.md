# Kask 
[![Build Status](https://travis-ci.org/Stvad/kask.svg?branch=master)](https://travis-ci.org/Stvad/kask)
[![codecov](https://codecov.io/gh/Stvad/kask/branch/master/graph/badge.svg)](https://codecov.io/gh/Stvad/kask)


A Kotlin library designed to improve an experience of developing Alexa skills on JVM.   
It's based on official [ASK SDK for Java](https://github.com/alexa/alexa-skills-kit-sdk-for-java).  
You can find the complete working example (Kotlin and Java) here: https://github.com/Stvad/hello-kask 

---

## Why would you want to use this

1. [Static, automatically instantiated version of your Skill model](#static-automatically-instantiated-version-of-your-skill-model)
1. [Functional handlers](#functional-request-handlers)
1. [Concise intent handler classes](#concise-intent-handler-classes)


---

### Static, automatically instantiated version of your Skill model 

Kask can generate code based on your skill model, creating representations for the Intents and Slots in your model.   
It will also perform instantiation of those classes for incoming request.

So your model is expressed explicitly in the code and you don't have to handle it's recreation from incoming request yourself.

#### Example

Say you have model with the following intent defined in it:

```json
{
  "name": "SetTimerIntent",
  "slots": [
    {
      "name": "timerDuration",
      "type": "AMAZON.DURATION",
      ...
    }
  ]
}
```

To handle this intent, if you are to use the vanilla ASK SDK for Java, you'd need write something like this:

```kotlin
class SetTimerIntentHandler : RequestHandler {
    override fun canHandle(input: HandlerInput) = input.matches(intentName("SetTimerIntent"))

    override fun handle(input: HandlerInput): Optional<Response> {
        val intentRequest = input.requestEnvelope.request as IntentRequest
        val period = parsePeriod(intentRequest.intent.slots["timerDuration"]?.value)
        //set timer...
        val speechText = "Timer was successfully set"
        return input.responseBuilder
                .withSpeech(speechText)
                .withSimpleCard("TimerSet", speechText)
                .build()
    }

    private fun parsePeriod(duration: String?): Period {
        TODO("...")
    }
}
```

If you're to use Kask you can achieve the same functionality in the following way:

```kotlin
class SetTimerIntentHandler : IntentRequestHandler<SetTimerIntent>(SetTimerIntent) {
    override fun handle(input: HandlerInput, intent: SetTimerIntent): Optional<Response> {
        val duration = intent.timerDuration.value
        //set timer...
        val speechText = "Timer was successfully set"
        return input.respond {
            withSpeech(speechText)
            withSimpleCard("TimerSet", speechText)
        }
    }
} 
```

To unpack this example a little:
* `SetTimerIntent` is an intent class generated by Kask. It has `timerDuration` property of `DurationSlot` type;
* `handle` function now accepts the instance of `SetTimerIntent` (it's instantiated by base class);
* You can now get the value of the slot by calling `intent.timerDuration.value` which would give you an instance of `Period?` (you don't need to parse it yourself).
* The `canHandle` function implementation is done for you by Kask. You can override it if you need to though.
 

### Functional request handlers

Kask provides you an ability to concisely handle various requests or intents via Functional Handlers.

#### Examples
**Handle LaunchRequest**  
*Kotlin*
```kotlin
respond(LaunchRequest::class) {
    val welcomeSpeech = "Hey there!"
    withSpeech(welcomeSpeech)
    withReprompt(welcomeSpeech)
    withSimpleCard("Hello Kask", welcomeSpeech)
}
//or 
handle(LaunchRequest::class) {
    val welcomeSpeech = "Hey there!"
    it.respond {
        withSpeech(welcomeSpeech)
        withReprompt(welcomeSpeech)
        withSimpleCard("Hello Kask", welcomeSpeech)
    }
}
```
*Java*
```java 
handle(input -> {
            String welcomeSpeech = "Hey There!";
            return input.getResponseBuilder()
                    .withSpeech(welcomeSpeech)
                    .withReprompt(welcomeSpeech)
                    .withSimpleCard("Hello Kask", welcomeSpeech)
                    .build();
        },
        LaunchRequest.class);

```

**Handle AMAZON.CancelIntent and AMAZON.StopIntent**  
*Kotlin*
```kotlin
respond("AMAZON.StopIntent", "AMAZON.CancelIntent") { withSpeech("OK!") }
```

*Java*
```java
handle(input -> input.getResponseBuilder()
                .withSpeech("OK!")
                .build(),
                "AMAZON.CancelIntent", "AMAZON.StopIntent");
```

### Concise intent handler classes

As in Functional Handlers above Kask can help you with defining `canHandle` function for common cases.

*Kotlin*
```kotlin
class FallBackIntentHandler : BasicIntentRequestHandler("AMAZON.FallbackIntent") {
    override fun handle(input: HandlerInput) = input.respond {
        withSpeech("I didn't quite catch that!")
    }
}
```  
*Java*
```java
public class FallBackIntentHandler extends BasicIntentRequestHandler {
    public FallBackIntentHandler() {
        super("AMAZON.FallbackIntent");
    }

    @Override
    public Optional<Response> handle(HandlerInput input) {
        return input.getResponseBuilder().withSpeech("I didn't quite catch that!").build();
    }
}
```

## How to get it (Gradle) 

### As a plugin

If you'd like to take advantage of code generation capabilities - you'd need to use Kask gradle plugin. (If you'd like to write a plugin for other build system or just want to use code generator as a library - refer to `KaskGenerator.kt`).

To do that you need make two changes to your gradle project: 

1. **Add jitpack repository to plugin repository configuration.**   
  If you didn't have this configuration explicitly specified before - you need to explicitly add `gradlePluginPortal` as well.  
  So a complete working example would look like:  
  **settings.gradle.kts**:
  
      ```kotlin
      pluginManagement {
          repositories {
              gradlePluginPortal()
              maven(url = "https://jitpack.io")
          }
      }
      ```

1. **Add plugin configuration to the `plugins` section**
 
    **build.gradle.kts:**
    ```kotlin
    plugins{
        id("org.stvad.kask") version "0.1.4"
    }
    ```
    
#### Configure code generation

To configure code generation from your skill model, you need to set a few settings on the `kask` gradle task:
* `packageName` - Package name for generated code
* `modelPath` - The path where your skill model is located  

**Example from [hello-kask](https://github.com/Stvad/hello-kask/blob/master/build.gradle.kts)**  
*Kotlin DSL*
```kotlin
tasks.withType<Kask> {
    packageName = "org.stvad.kask.example.model" 
    modelPath.set(layout.projectDirectory.dir("models").file("en-US.json")) 
}
```

*Groovy DSL*
```groovy
kask {
    packageName = "org.stvad.kask.example.model" 
    modelPath.set(layout.projectDirectory.dir("models").file("en-US.json"))
}
```

### As a library

If you want to use Kask only as a library (e.g. to play with functional handlers or code generator as a library). 
You need to **add jitpack repository to your repositories** and **add dependency declaration for Kask**.

Example:
```kotlin
repositories {
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation("com.github.Stvad:kask:0.1.0") 
    
}
```


## References

1. [Complete working example of using Kask](https://github.com/Stvad/hello-kask)
1. [A skill I'm developing using Kask](https://github.com/Stvad/alexa-life-advice)
1. [Alexa Skills Kit SDK for Java](https://github.com/alexa/alexa-skills-kit-sdk-for-java)
