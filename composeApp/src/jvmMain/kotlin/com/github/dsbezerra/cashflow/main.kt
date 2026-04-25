package com.github.dsbezerra.cashflow

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.github.dsbezerra.cashflow.core.di.appModules
import org.koin.core.context.startKoin

fun main() {
    startKoin {
        modules(appModules)
    }
    application {
        val windowState = rememberWindowState(size = DpSize(900.dp, 600.dp))
        Window(
            onCloseRequest = ::exitApplication,
            title = "CashFlow",
            state = windowState,
        ) {
            window.minimumSize = java.awt.Dimension(900, 600)
            App()
        }
    }
}
