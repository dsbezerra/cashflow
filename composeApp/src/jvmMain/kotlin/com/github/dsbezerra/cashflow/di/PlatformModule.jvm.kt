package com.github.dsbezerra.cashflow.di

import com.github.dsbezerra.cashflow.data.db.DriverFactory
import org.koin.dsl.module

actual val platformModule = module {
    single { DriverFactory() }
}
