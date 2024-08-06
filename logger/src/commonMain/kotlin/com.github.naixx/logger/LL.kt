/*
 * Created by Rostislav Chekan
 *
 * Copyright (c) Rostislav Chekan 2024. All rights reserved.
 */

package com.github.naixx.logger

import io.github.aakira.napier.Napier

internal expect fun getMethodAndLine(): String?

object LL {

    fun e(a: Any?) {
        val method = getMethodAndLine()
        Napier.e { a?.toString() + (method?.let { "--->($method" } ?: "") }
    }

    fun e(throwable: Throwable?, a: Any?) {
        val method = getMethodAndLine()
        Napier.e(throwable) { a?.toString() + (method?.let { "--->($method" } ?: "") }
    }

    fun d(a: Any?) {
        val method = getMethodAndLine()
        Napier.d { a?.toString() + (method?.let { "--->($method" } ?: "") }
    }

    fun w(a: Any?) {
        val method = getMethodAndLine()
        Napier.w { a?.toString() + (method?.let { "--->($method" } ?: "") }
    }

    fun i(a: Any?) {
        val method = getMethodAndLine()
        Napier.i { a?.toString() + (method?.let { "--->($method" } ?: "") }
    }
}
