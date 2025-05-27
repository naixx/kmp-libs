package com.github.naixx.core

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import nl.jacobras.humanreadable.HumanReadable
import kotlin.time.Duration

val Instant.isInPast
    get() = Clock.System.now() >= this

val Instant.relative
    get() = HumanReadable.timeAgo(this)

val Number.abbreviation
    get() = if (this.toDouble() < 1000.0) this.toString().removeTrailingZeros() else HumanReadable.abbreviation(this)

/**
 * Formats the given [number].
 *
 * For example: 1_000_000.34 returns:
 * - "1,000,000.34" for EN
 * - "1 000 000.34" for FR
 * - "1.000.000,34" for NL
 *
 * @param number The number to format.
 * @param decimals The number of decimals to use in formatting.
 * @return a formatted string
 */
fun Number.number(decimals: Int = 0) = HumanReadable.number(this, decimals)
val Number.number
    get() = HumanReadable.number(this)

val Duration.duration
    get() = HumanReadable.duration(this)

fun Number.abbreviation(decimals: Int = 0) = HumanReadable.abbreviation(this, decimals).removeTrailingZeros()

fun String.removeTrailingZeros(): String {
    val decimal = HumanReadableRes.string.decimalSymbol
    val escapedDecimal = Regex.escape(decimal)

    return replace(
        Regex("($escapedDecimal\\d*?)0+([A-Za-z]|$)"),
        "$1$2"
    ) // Trailing zeros before letter/end "1.200K" → "1.2K"
        .replace(Regex("$escapedDecimal+?([A-Za-z]|$)"), "$1")     // Decimal point followed by letter/end "1.K" → "1K"
        .replace(Regex("$escapedDecimal$"), "")                    // Lone decimal at end "12." → "12"
}
