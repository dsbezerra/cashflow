package com.github.dsbezerra.cashflow.data.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.github.dsbezerra.cashflow.db.CashFlowDatabase
import java.io.File

actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        val dbFile = File("cashflow.db")
        val isNew = !dbFile.exists()
        return JdbcSqliteDriver("jdbc:sqlite:cashflow.db").also { driver ->
            if (isNew) CashFlowDatabase.Companion.Schema.create(driver)
        }
    }
}