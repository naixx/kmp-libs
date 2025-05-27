package com.github.naixx.core

import nl.jacobras.humanreadable.HumanReadable.number
import kotlin.test.Test
import kotlin.test.assertEquals

class HumanReadableTest {
    @Test
    fun `should not abbreviate numbers less than 1000`(){
        assertEquals("0.12", 0.12.abbreviation)
        assertEquals("2K", 2194.abbreviation)
        assertEquals("0", 0.abbreviation)
        assertEquals("5M", 5000000.abbreviation)
    }

    @Test
    fun `removeTrailingZeros should remove trailing zeros and decimal separator correctly`() {
        assertEquals("123.45", "123.450".removeTrailingZeros())
        assertEquals("123", "123.0".removeTrailingZeros())
        assertEquals("123", "123.00".removeTrailingZeros())
        assertEquals("0.5", "0.50".removeTrailingZeros())
        assertEquals("0", "0.0".removeTrailingZeros())
        assertEquals("10000", "10000.0".removeTrailingZeros())
        assertEquals("123", "123".removeTrailingZeros())
        assertEquals("0.123", "0.12300".removeTrailingZeros())
    }

    @Test
    fun `abbreviation should format numbers correctly`() {
        assertEquals("1K", 1000.abbreviation())
        assertEquals("1.2K", 1234.abbreviation(1))
        assertEquals("1.23K", 1234.abbreviation(2))
        assertEquals("1M", 1000000.abbreviation())
        assertEquals("1.5M", 1500000.abbreviation(2))
        assertEquals("1B", 1000000000.abbreviation(3))
        assertEquals("1.003B", 1002900000.abbreviation(3))
        assertEquals("1.2B", 1234567890.abbreviation(1))
        assertEquals("123", 123.abbreviation())
        assertEquals("12.3", 12.34.abbreviation(1))
        assertEquals("0", 0.abbreviation())
    }

}
