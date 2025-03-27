/*
 * Created by Rostislav Chekan
 *
 * Copyright (c) Rostislav Chekan 2024. All rights reserved.
 */

package com.github.naixx.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.contracts.*

typealias RetryButtonContent = @Composable (UiState.Error) -> Unit
typealias LoadingContent = @Composable LoadingScope.() -> Unit

sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data class Success<out T>(val data: T) : UiState<T>()
    data class Error(val t: Throwable? = null, val retry: () -> Unit = {}) : UiState<Nothing>()
}

val <T> UiState<T>.ok
    get() = this is UiState.Loading || this is UiState.Success

data class RetryButtonTheme(
    val loadingContent: LoadingContent = {},
    val retryButtonContent: RetryButtonContent = { DefaultRetryButton(it) },
    val rowSpace: Dp = 8.dp,
    val rowPadding: Dp = 16.dp,
    val timesSpace: Dp = 8.dp,
)

@Composable
private fun DefaultRetryButton(error: UiState.Error) {
    Button(onClick = error.retry) {
        Text("Retry")
    }
}

val LocalRetryButtonTheme: ProvidableCompositionLocal<RetryButtonTheme?> = staticCompositionLocalOf { RetryButtonTheme() }

@Composable
fun <T> UiState<T>.Render(
    loading: @Composable LoadingScope.() -> Unit = LocalRetryButtonTheme.current?.loadingContent ?: {},
    error: RetryButtonContent = LocalRetryButtonTheme.current?.retryButtonContent ?: {},
    content: @Composable (T) -> Unit
) {
    when (this) {
        is UiState.Error   -> {
            error(this)
        }

        UiState.Loading    -> {
            LoadingScope().loading()
        }

        is UiState.Success -> content(this.data)
    }
}

@OptIn(ExperimentalContracts::class)
inline fun <T, R> UiState<T>.map(transform: (value: T) -> R): UiState<R> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }
    return when (this) {
        is UiState.Success<T> -> UiState.Success(transform(this.data))
        is UiState.Loading    -> UiState.Loading
        is UiState.Error      -> this
    }
}

@OptIn(ExperimentalContracts::class)
inline fun <T, R> UiState<T>.mapSuccess(transform: (value: T) -> R): R? {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }
    return when (this) {
        is UiState.Success<T> -> transform(this.data)
        else                  -> null
    }
}

fun <T> UiState<T>.onRetry(newRetry: () -> Unit) = if (this is UiState.Error) UiState.Error(t, newRetry) else this

fun <T> T.uiState() = UiState.Success(this)

class LoadingScope {

    @Composable
    operator fun Int.times(content: @Composable () -> Unit) {
        repeat(this@times) {
            content()
            Spacer(Modifier.size(LocalRetryButtonTheme.current?.rowSpace ?: 0.dp))
        }
    }

    @Composable
    infix fun Int.row(content: @Composable () -> Unit) {
        Row(Modifier.padding(LocalRetryButtonTheme.current?.rowPadding ?: 0.dp)) {
            repeat(this@row) {
                content()
                Spacer(Modifier.size(LocalRetryButtonTheme.current?.rowSpace ?: 0.dp))
            }
        }
    }

    @Composable
    fun Int.row(spacedBy: Dp = 8.dp, content: @Composable () -> Unit) {
        Row(Modifier.padding(LocalRetryButtonTheme.current?.rowPadding ?: 0.dp)) {
            repeat(this@row) {
                content()
                Spacer(Modifier.size(spacedBy))
            }
        }
    }
}

suspend fun <R> catching(retry: () -> Unit = {}, block: suspend () -> R): UiState<R> {
    return try {
        UiState.Success(block())
    } catch (t: TimeoutCancellationException) {
        UiState.Error(t, retry)
    } catch (c: CancellationException) {
        throw c
    } catch (e: Throwable) {
        UiState.Error(e, retry)
    }
}

fun <T> CoroutineScope.produce(flow: MutableStateFlow<UiState<T>>, retry: () -> Unit = {}, block: suspend () -> T) {
    this@produce.launch {
        flow.value = UiState.Loading
        flow.value = catching(retry) { block() }
    }
}

@Composable
fun <T> produce(key1: Any?, retry: () -> Unit = {}, block: suspend () -> T): UiState<T> {
    var state: UiState<T> by remember { mutableStateOf(UiState.Loading) }
    LaunchedEffect(key1 = key1) {
        state = UiState.Loading
        state = catching(retry) { block() }
    }
    return state
}

@Composable
fun <T> produce(key1: Any?, key2: Any?, retry: () -> Unit = {}, block: suspend () -> T): UiState<T> {
    var state: UiState<T> by remember { mutableStateOf(UiState.Loading) }
    LaunchedEffect(key1 = key1, key2 = key2) {
        state = UiState.Loading
        state = catching(retry) { block() }
    }
    return state
}

@Composable
fun <T> produce(key1: Any?, key2: Any?, key3: Any?,retry: () -> Unit = {}, block: suspend () -> T): UiState<T> {
    var state: UiState<T> by remember { mutableStateOf(UiState.Loading) }
    LaunchedEffect(key1 = key1, key2 = key2, key3 = key3) {
        state = UiState.Loading
        state = catching(retry) { block() }
    }
    return state
}

@Composable
fun <T> produce(retry: () -> Unit = {}, block: suspend () -> T): UiState<T> {
    var state: UiState<T> by remember { mutableStateOf(UiState.Loading) }
    LaunchedEffect(Unit) {
        state = UiState.Loading
        state = catching(retry) { block() }
    }
    return state
}
