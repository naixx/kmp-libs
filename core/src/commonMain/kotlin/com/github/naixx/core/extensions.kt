package com.github.naixx.core

import kotlinx.datetime.*
import nl.jacobras.humanreadable.HumanReadable
import kotlin.time.Duration

val Instant.isInPast
    get() = Clock.System.now() >= this

val Instant.relative
    get() = HumanReadable.timeAgo(this)

val Number.abbreviation
    get() = HumanReadable.abbreviation(this)

val Duration.duration
    get() = HumanReadable.duration(this)
