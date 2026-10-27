package com.carlosmarins.fivedab.ui.components

import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.wear.compose.foundation.lazy.ScalingLazyListState
import kotlinx.coroutines.launch

/**
 * Connects the Galaxy Watch8 Classic physical rotating bezel to a [ScalingLazyListState]
 * for smooth list scrolling.
 */
@Composable
fun Modifier.bezelScrollableList(listState: ScalingLazyListState): Modifier {
    val focusRequester = remember { FocusRequester() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        runCatching { focusRequester.requestFocus() }
    }

    return this
        .onRotaryScrollEvent { event ->
            coroutineScope.launch {
                listState.scrollBy(event.verticalScrollPixels)
            }
            true
        }
        .focusRequester(focusRequester)
        .focusable()
}

/**
 * Connects the Galaxy Watch8 Classic physical rotating bezel to a custom value/step handler
 * (used during Active Workout & Step Editor to adjust reps, timers, or skip sets).
 */
@Composable
fun Modifier.bezelStepController(
    onRotaryDelta: (Float) -> Unit
): Modifier {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        runCatching { focusRequester.requestFocus() }
    }

    return this
        .onRotaryScrollEvent { event ->
            onRotaryDelta(event.verticalScrollPixels)
            true
        }
        .focusRequester(focusRequester)
        .focusable()
}
