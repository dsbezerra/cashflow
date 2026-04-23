package com.github.dsbezerra.cashflow.ui.common

import androidx.compose.foundation.ScrollbarAdapter
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.github.dsbezerra.cashflow.isDesktop

@Composable
fun BoxScope.DesktopVerticalScrollbar(adapter: ScrollbarAdapter) {
    if (isDesktop) {
        VerticalScrollbar(
            adapter = adapter,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight(),
        )
    }
}
