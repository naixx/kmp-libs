/*
 * Created by Rostislav Chekan
 *
 * Copyright (c) Rostislav Chekan 2024. All rights reserved.
 */

package com.github.naixx.logger

import io.github.aakira.napier.Napier

data class Source(val tag: String, val line: String?)

internal expect fun getMethodAndLine(): Source?

object LL {

    fun e(a: Any?) {
        val source = getMethodAndLine()
        Napier.e(tag = source?.tag) { a?.toString() + (source?.let { "--->(${source.line}" } ?: "") }
    }

    fun e(throwable: Throwable?, a: Any?) {
        val source = getMethodAndLine()
        Napier.e(tag = source?.tag, throwable = throwable) { a?.toString() + (source?.let { "--->(${source.line}" } ?: "") }
    }

    fun d(a: Any?) {
        val source = getMethodAndLine()
        Napier.d(tag = source?.tag) { a?.toString() + (source?.let { "--->(${source.line}" } ?: "") }
    }

    fun w(a: Any?) {
        val source = getMethodAndLine()
        Napier.w(tag = source?.tag) { a?.toString() + (source?.let { "--->(${source.line}" } ?: "") }
    }

    fun w(throwable: Throwable?, a: Any?) {
        val source = getMethodAndLine()
        Napier.w(tag = source?.tag, throwable = throwable) { a?.toString() + (source?.let { "--->(${source.line}" } ?: "") }
    }

    fun i(a: Any?) {
        val source = getMethodAndLine()
        Napier.i(tag = source?.tag) { a?.toString() + (source?.let { "--->(${source.line}" } ?: "") }
    }
}
