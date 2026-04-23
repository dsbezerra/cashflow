package com.github.dsbezerra.cashflow.data.db

actual class DriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver =
        AndroidSqliteDriver(CashFlowDatabase.Schema, context, "cashflow.db")
}