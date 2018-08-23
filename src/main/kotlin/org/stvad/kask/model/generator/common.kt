package org.stvad.kask.model.generator

import com.squareup.kotlinpoet.ParameterSpec
import org.stvad.kask.model.Intent
import org.stvad.kask.model.Slot

val askIntentParameter = ParameterSpec.builder(Intent::askIntent.name, com.amazon.ask.model.Intent::class).build()
val askSlotParameter = ParameterSpec.builder(Slot<Any>::askSlot.name, com.amazon.ask.model.Slot::class).build()
