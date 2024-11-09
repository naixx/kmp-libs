/*
 * Created by Rostislav Chekan
 *
 * Copyright (c) Rostislav Chekan 2024. All rights reserved.
 */

package com.github.naixx.compose

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.AnnotatedString.Builder
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import com.github.naixx.compose.InlinedText.UrlHolder
import kotlinx.coroutines.*

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
    val uriHandler = LocalUriHandler.current

    if (text.getStringAnnotations("URL", 0, text.length - 1).isNotEmpty())
    // Text(text, modifier = modifier, style = style, inlineContent = inlinedContent)
        ClickableText2(text, modifier = modifier, color = color, style = style, inlineContent = inlinedContent) { offset ->
            text.getStringAnnotations("URL", offset, offset)
                .firstOrNull()?.let { annotation ->
                    uriHandler.openUri(annotation.item)
                }
        }
    else
        Text(text, modifier = modifier, style = style, inlineContent = inlinedContent)
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

    class UrlHolder(val text: String, val url: String)

    operator fun String.get(url: String): UrlHolder = UrlHolder(this, url)

    operator fun UrlHolder.unaryMinus()
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

    override fun UrlHolder.unaryMinus() {
        with(builder) {
            val annotationTag = "URL"
            pushStringAnnotation(tag = annotationTag, annotation = url)
            withStyle(
                style = SpanStyle(
                    color = secondary,
                    textDecoration = TextDecoration.Underline,
                )
            ) {
                append(text)
            }
            // when pop is called it means the end of annotation with current tag
            pop()
        }
    }

    internal fun build() = builder.toAnnotatedString() to inlinedContent.toMap()
}

@Composable
private fun ClickableText2(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    style: TextStyle = TextStyle.Default,
    softWrap: Boolean = true,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    inlineContent: Map<String, InlineTextContent> = mapOf(),
    onTextLayout: (TextLayoutResult) -> Unit = {},
    onClick: (Int) -> Unit
) {
    val layoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }
    val pressIndicator = Modifier.pointerInput(onClick) {
        detectTapGestures { pos ->
            layoutResult.value?.let { layoutResult ->
                onClick(layoutResult.getOffsetForPosition(pos))
            }
        }
    }
    val textColor = color.takeOrElse {
        style.color.takeOrElse {
            LocalContentColor.current
        }
    }


    BasicText(
        text = text,
        modifier = modifier.then(pressIndicator),
        style = style.merge(color = textColor),
        softWrap = softWrap,
        overflow = overflow,
        maxLines = maxLines,
        inlineContent = inlineContent,
        onTextLayout = {
            layoutResult.value = it
            onTextLayout(it)
        }
    )
}

private fun MultiParagraph.containsWithinBounds(positionOffset: Offset): Boolean =
    positionOffset.let { (x, y) -> x > 0 && y >= 0 && x <= width && y <= height }

/**
 * Detects pointer events that result from pointer movements and feed said events to the
 * [onMove] function. When multiple pointers are being used, only the first one is tracked.
 * If the first pointer is then removed, the second pointer will take its place as the first
 * pointer and be tracked.
 *
 * @param pointerEventPass which pass to capture the pointer event from, see [PointerEventPass]
 * @param onMove function that handles the position of move events
 */
internal suspend fun PointerInputScope.detectMoves(
    pointerEventPass: PointerEventPass = PointerEventPass.Initial,
    onMove: (Offset) -> Unit
) = coroutineScope {
    val currentContext = currentCoroutineContext()
    awaitPointerEventScope {
        var previousPosition: Offset? = null
        while (currentContext.isActive) {
            val event = awaitPointerEvent(pointerEventPass)
            when (event.type) {
                PointerEventType.Move, PointerEventType.Enter, PointerEventType.Exit ->
                    event.changes.first().position
                        .takeUnless { it == previousPosition }
                        ?.let { position ->
                            previousPosition = position
                            onMove(position)
                        }
            }
        }
    }
}
