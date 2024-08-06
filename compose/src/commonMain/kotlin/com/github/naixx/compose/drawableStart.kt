/*
 * Created by Rostislav Chekan
 *
 * Copyright (c) Rostislav Chekan 2024. All rights reserved.
 */

package com.github.naixx.compose

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.unit.*
import org.jetbrains.compose.resources.painterResource

fun Modifier.drawableStart(
    painter: Painter,
    imageSize: Dp,
    padding: Dp,
    tint: Color? = null
): Modifier {
    return this
        .drawWithCache {
            val imageSizePx = imageSize.toPx()
            onDrawWithContent {
                val drawableY = (size.height - imageSizePx) / 2
                with(painter) {
                    withTransform({
                        translate(0f, drawableY)
                    }) {
                        draw(
                            size = Size(imageSizePx, imageSizePx),
                            colorFilter = tint?.let { ColorFilter.tint(tint) }
                        )
                    }
                }
                // Draw the original content
                drawContent()
            }
        }
        .padding(start = imageSize + padding)
}

@Composable
fun Modifier.drawableStart(
    icon: ImageVector,
    imageSize: Dp = 16.dp,
    padding: Dp = 4.dp,
    tint: Color? = LocalContentColor.current
): Modifier {
    val painter = rememberVectorPainter(image = icon)
    return drawableStart(painter, imageSize, padding, tint)
}

fun Modifier.drawableEnd(
    painter: Painter,
    imageSize: Dp,
    padding: Dp,
    tint: Color? = null
): Modifier {
    return this
        .padding(end = imageSize + padding)
        .drawWithCache {
            val imageSizePx = imageSize.toPx()
            val paddingPx = padding.toPx()
            onDrawWithContent {
                // Draw the original content
                drawContent()
                val drawableX = size.width + paddingPx
                val drawableY = (size.height - imageSizePx) / 2
//                // Draw the vector drawable using the painter
//                with(painter) {
//
//                    draw(
//                        Offset(drawableX, drawableY),
//                        size = Size(imageSizePx, imageSizePx)
//                    )
//                }
                //  drawIntoCanvas { canvas ->
                with(painter) {
                    withTransform({
                        translate(drawableX, drawableY)
                    }) {
                        draw(size = Size(imageSizePx, imageSizePx),
                            colorFilter = tint?.let { ColorFilter.tint(tint) })
                    }
                }
                // }
            }
        }
}


@Composable
fun Modifier.drawableEnd(
    icon: ImageVector,
    imageSize: Dp = 16.dp,
    padding: Dp = 4.dp,
    tint: Color? = LocalContentColor.current
): Modifier {
    val painter = rememberVectorPainter(image = icon)
    return drawableEnd(painter, imageSize, padding, tint)
}
