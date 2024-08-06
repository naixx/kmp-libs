package com.github.naixx.logger

internal actual fun getMethodAndLine(): String? {
    val line = Thread.currentThread().stackTrace.drop(4).take(1).toString().trim('[', ']')
    val strings = line.split('(')
    return strings.getOrNull(1)
}
