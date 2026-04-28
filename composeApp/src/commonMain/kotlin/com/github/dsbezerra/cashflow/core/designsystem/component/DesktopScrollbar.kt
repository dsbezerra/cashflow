package com.github.dsbezerra.cashflow.core.designsystem.component

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable

@Composable
expect fun BoxScope.DesktopVerticalScrollbar(scrollState: ScrollState)

@Composable
expect fun BoxScope.DesktopVerticalScrollbar(lazyListState: LazyListState)
