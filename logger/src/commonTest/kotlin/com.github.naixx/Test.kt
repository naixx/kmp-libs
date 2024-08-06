package com.github.naixx

import com.github.naixx.logger.LL
import io.github.aakira.napier.*
import kotlin.test.*
import kotlin.test.Test

class Test {

    @BeforeTest
    fun before() {
        Napier.base(DebugAntilog())
    }

    @Test
    fun test() {
        LL.e("Hello log")
    }
}
