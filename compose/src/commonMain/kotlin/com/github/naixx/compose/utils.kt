package com.github.naixx.compose

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.material3.ButtonColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.*
import kotlin.contracts.*

@Composable
fun ButtonColors.animateEnabled(enabled: Boolean, durationMillis: Int = 100): ButtonColors {
    val animatedButtonColor = animateColorAsState(
        targetValue = if (enabled) this.containerColor else this.disabledContainerColor,
        animationSpec = tween(durationMillis, 0, LinearEasing)
    )
    val colors = this.copy(
        containerColor = animatedButtonColor.value,
        disabledContainerColor = animatedButtonColor.value,
    )
    return colors
}

val ImageVector.painter
    @Composable get() = rememberVectorPainter(image = this)

@OptIn(ExperimentalContracts::class)
inline fun Modifier.check(condition: Boolean, block: Modifier.() -> Modifier): Modifier {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return if (condition)
        block()
    else
        this
}
