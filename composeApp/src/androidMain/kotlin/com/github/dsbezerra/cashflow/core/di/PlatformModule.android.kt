package com.github.dsbezerra.cashflow.core.di

import com.github.dsbezerra.cashflow.core.data.db.DriverFactory
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val platformModule = module {
    single { DriverFactory(androidContext()) }
}
