package com.github.dsbezerra.cashflow

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.github.dsbezerra.cashflow.di.appModules
import org.koin.core.context.startKoin

fun main() {
    startKoin {
        modules(appModules)
    }
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "CashFlow",
        ) {
            App()
        }
    }
}
