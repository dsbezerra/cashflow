package com.github.dsbezerra.cashflow.core.di

import co.touchlab.kermit.Logger
import org.koin.dsl.module

val loggingModule = module {
    single { Logger.withTag("CashFlow") }
}
