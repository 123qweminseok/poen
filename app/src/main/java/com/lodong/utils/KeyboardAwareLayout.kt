package com.lodong.utils

import android.app.Activity
import android.graphics.Rect
import android.view.ViewTreeObserver
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity

@Composable
fun KeyboardAwareLayout(
    modifier: Modifier = Modifier,
    content: @Composable (PaddingValues) -> Unit
) {
    val keyboardHeight = rememberKeyboardHeight()
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = with(LocalDensity.current) { keyboardHeight.toDp() })
    ) {
        content(PaddingValues(bottom = with(LocalDensity.current) { keyboardHeight.toDp() }))
    }
}

@Composable
fun rememberKeyboardHeight(): Int {
    val context = LocalContext.current
    val density = LocalDensity.current
    val keyboardHeight = remember { mutableStateOf(0) }

    DisposableEffect(Unit) {
        val rootView = (context as? Activity)?.window?.decorView ?: return@DisposableEffect onDispose {}
        val listener = ViewTreeObserver.OnGlobalLayoutListener {
            val rect = Rect()
            rootView.getWindowVisibleDisplayFrame(rect)
            val screenHeight = rootView.height
            val heightDiff = screenHeight - rect.bottom
            keyboardHeight.value = if (heightDiff > screenHeight * 0.15) heightDiff else 0
        }
        rootView.viewTreeObserver.addOnGlobalLayoutListener(listener)
        onDispose {
            rootView.viewTreeObserver.removeOnGlobalLayoutListener(listener)
        }
    }

    return with(density) { keyboardHeight.value.toDp().roundToPx() }
}
