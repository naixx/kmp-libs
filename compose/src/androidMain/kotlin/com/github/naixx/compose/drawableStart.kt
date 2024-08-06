@file:JvmName("drawableStartAndroid")
package com.github.naixx.compose

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.*
import com.github.naixx.compose.*

@SuppressLint("ModifierFactoryUnreferencedReceiver")
@Composable
fun Modifier.drawableStart(
    id: Int,
    imageSize: Dp = 24.dp,
    padding: Dp = 0.dp,
    tint: Color? = null
): Modifier {
    val painter = painterResource(id = id)

    return drawableStart(painter, imageSize, padding, tint)
}

@SuppressLint("ModifierFactoryUnreferencedReceiver")
@Composable
fun Modifier.drawableEnd(
    id: Int,
    imageSize: Dp,
    padding: Dp
): Modifier {
    val painter = painterResource(id = id)
    return drawableEnd(painter, imageSize, padding)
}
