package com.github.naixx.compose

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertIs

@RunWith(RobolectricTestRunner::class)
@Config(packageName = "com.github.naixx.compose.test")
class UiStateTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `verify retry works when enabled`() {
        var computationCount = 0

        var shouldFail by mutableStateOf(true)
        lateinit var uiState: UiState<Int>

        composeTestRule.setContent {
            uiState = produce(1, 2, 3, retry = true) {
                computationCount++
                if (shouldFail) throw RuntimeException("fail!")
                1
            }
        }

        // Wait for error state
        composeTestRule.runOnIdle {
            val state = uiState
            check(state is UiState.Error) { "Should be error initially" }
            assertEquals(1, computationCount)
        }

        // Disable failure and retry
        composeTestRule.runOnIdle { shouldFail = false }
        composeTestRule.runOnIdle { (uiState as UiState.Error).retry() }
        composeTestRule.runOnIdle {
            assertEquals(UiState.Success(1), uiState)
            assertEquals(2, computationCount)
        }
    }

    @Test
    fun `verify retry doesn't work by default and when disabled`() {
        var computationCount = 0

        var shouldFail by mutableStateOf(true)
        lateinit var uiState: UiState<Int>

        composeTestRule.setContent {
            uiState = produce(1, 2, 3) {
                computationCount++
                if (shouldFail) throw RuntimeException("fail!")
                1
            }
        }

        // Wait for error state
        composeTestRule.runOnIdle {
            val state = uiState
            check(state is UiState.Error) { "Should be error initially" }
            assertEquals(1, computationCount)
        }

        // Disable failure and retry
        composeTestRule.runOnIdle { shouldFail = false }
        composeTestRule.runOnIdle { (uiState as UiState.Error).retry() }
        composeTestRule.runOnIdle {
            assertIs<UiState.Error>(uiState)
            assertEquals(1, computationCount)
        }
    }
}
