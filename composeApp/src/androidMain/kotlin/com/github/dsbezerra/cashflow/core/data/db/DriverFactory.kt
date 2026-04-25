package com.github.dsbezerra.cashflow.core.data.db

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.github.dsbezerra.cashflow.db.CashFlowDatabase

actual class DriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver =
        AndroidSqliteDriver(CashFlowDatabase.Schema, context, "cashflow.db")
}