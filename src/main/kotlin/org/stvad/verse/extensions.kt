package org.stvad.verse

import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec

fun ParameterSpec.toProperty() = PropertySpec.builder(name, type).initializer(name).build()

fun Iterable<ParameterSpec>.toInvocation() = map(ParameterSpec::name).joinToString()
fun Iterable<ParameterSpec>.toProperties() = map { it.toProperty() }

