package com.github.dsbezerra.cashflow.ui.designsystem.components.loader

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.github.dsbezerra.cashflow.ui.theme.CashFlowTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DSLoader(
    label: String? = null,
    indicator: @Composable () -> Unit = { LoadingIndicator() }
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        indicator()
        if (label != null) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DSFullscreenLoader(
    innerPadding: PaddingValues? = null,
    label: String? = null,
    indicator: @Composable () -> Unit = { LoadingIndicator() }
) {
    Box(
        modifier = Modifier.fillMaxSize()
            .then(
                if (innerPadding != null) {
                    Modifier.padding(innerPadding)
                } else {
                    Modifier
                }
            ), contentAlignment = Alignment.Center
    ) {
        DSLoader(
            label = label,
            indicator = indicator
        )
    }
}

@Preview
@Composable
private fun PreviewDSLoader() {
    CashFlowTheme {
        DSLoader()
    }
}