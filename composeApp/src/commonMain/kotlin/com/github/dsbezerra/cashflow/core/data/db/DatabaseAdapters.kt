package com.github.dsbezerra.cashflow.core.data.db

import app.cash.sqldelight.ColumnAdapter

fun <T : Enum<T>> enumColumnAdapter(valueOf: (String) -> T): ColumnAdapter<T, String> =
    object : ColumnAdapter<T, String> {
        override fun decode(databaseValue: String): T = valueOf(databaseValue)
        override fun encode(value: T): String = value.name
    }
