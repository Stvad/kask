package org.stvad.kask

open class KaskException(message: String? = null, cause: Throwable? = null) : RuntimeException(message, cause)

class InvalidModelException(message: String? = null, cause: Throwable? = null) : KaskException(message, cause)

class SlotMissingException(message: String? = null, cause: Throwable? = null) : KaskException(message, cause)