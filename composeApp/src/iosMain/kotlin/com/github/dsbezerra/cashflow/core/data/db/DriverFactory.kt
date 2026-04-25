package com.github.dsbezerra.cashflow.core.data.db

actual class DriverFactory {
    actual fun createDriver(): SqlDriver =
        NativeSqliteDriver(CashFlowDatabase.Schema, "cashflow.db")
}