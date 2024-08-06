/*
 * Created by Rostislav Chekan
 *
 * Copyright (c) Rostislav Chekan 2024. All rights reserved.
 */

package com.github.naixx.compose

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.*
import androidx.compose.ui.text.AnnotatedString.Builder
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.*

@Target(AnnotationTarget.TYPE)
@DslMarker
annotation class InlinedTextDsl

@Composable
fun InlinedText(
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    style: TextStyle = LocalTextStyle.current,
    builder: (@InlinedTextDsl InlinedText).() -> Unit
) {
    val (text: AnnotatedString, inlinedContent: Map<String, InlineTextContent>)
            = InlinedTextImpl(MaterialTheme.colorScheme.secondary).apply(builder).build()

    Text(text, modifier = modifier, color = color, style = style, inlineContent = inlinedContent)
}

interface InlinedText {

    operator fun String.unaryPlus()

    operator fun AnnotatedString.unaryPlus()

    operator fun ImageVector.unaryPlus() {
        icon(this, 16.sp)
    }

    fun icon(image: ImageVector, size: TextUnit)

    //underline and select text
    operator fun String.unaryMinus()
}

private class InlinedTextImpl(val secondary: Color) : InlinedText {

    private val builder: AnnotatedString.Builder = Builder()
    private val inlinedContent = mutableMapOf<String, InlineTextContent>()
    private var counter = 0

    override operator fun String.unaryPlus() {
        builder.append(this)
    }

    override operator fun AnnotatedString.unaryPlus() {
        builder.append(this)
    }

    override operator fun ImageVector.unaryPlus() {
        icon(this, 16.sp)
    }

    override fun icon(image: ImageVector, size: TextUnit) {
        val id = "<<icon$counter>>"
        builder.appendInlineContent(id, "[icon]")
        inlinedContent[id] = InlineTextContent(
            Placeholder(
                width = size,
                height = size,
                placeholderVerticalAlign = PlaceholderVerticalAlign.Center
            )
        ) {
            val density = LocalDensity.current
            val dp = with(density) { size.toDp() }
            Icon(image, null, Modifier.size(dp))
        }
        counter++
    }

    override fun String.unaryMinus() {
        with(builder) {
            withStyle(
                style = SpanStyle(
                    color = secondary,
                    textDecoration = TextDecoration.Underline,
                )
            ) {
                append(this@unaryMinus)
            }
        }
    }

    internal fun build() = builder.toAnnotatedString() to inlinedContent.toMap()
}

