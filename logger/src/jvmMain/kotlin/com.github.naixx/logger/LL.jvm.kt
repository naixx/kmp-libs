package com.github.naixx.logger

import java.util.regex.Pattern

internal actual fun getMethodAndLine(): Source? {
    val traceElement = Thread.currentThread().stackTrace.drop(4).firstOrNull() ?: return null
    val line = traceElement.toString()//.trim('[', ']')
    val strings = line.split('(')
    return Source(createStackElementTag(traceElement.className) + "$" + traceElement.methodName, strings.getOrNull(1))
}

private val anonymousClass = Pattern.compile("(\\$\\d+)+$")
internal fun createStackElementTag(className: String): String {
    var tag = className
    val m = anonymousClass.matcher(tag)
    if (m.find()) {
        tag = m.replaceAll("")
    }
    return tag.substring(tag.lastIndexOf('.') + 1)
}
