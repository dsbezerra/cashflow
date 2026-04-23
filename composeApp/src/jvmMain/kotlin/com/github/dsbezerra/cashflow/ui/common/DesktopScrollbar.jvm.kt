package com.github.dsbezerra.cashflow.ui.common

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.v2.ScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
actual fun BoxScope.DesktopVerticalScrollbar(scrollState: ScrollState) {
    DesktopVerticalScrollbar(rememberScrollbarAdapter(scrollState))
}

@Composable
actual fun BoxScope.DesktopVerticalScrollbar(lazyListState: LazyListState) {
    DesktopVerticalScrollbar(rememberScrollbarAdapter(lazyListState))
}

@Composable
fun BoxScope.DesktopVerticalScrollbar(adapter: ScrollbarAdapter) {
    VerticalScrollbar(
        adapter = adapter,
        modifier = Modifier
            .align(Alignment.CenterEnd)
            .fillMaxHeight(),
    )
}