package com.github.dsbezerra.cashflow.data.db

actual class DriverFactory {
    actual fun createDriver(): SqlDriver =
        NativeSqliteDriver(CashFlowDatabase.Schema, "cashflow.db")
}