package org.stvad.kask.model.generator.utils

import org.stvad.kask.model.DurationSlot
import org.stvad.kask.model.SlotDefinition

fun createDummySlot(name: String = "dummyName", type: String = DurationSlot.type) = SlotDefinition(name, type, emptyList())
