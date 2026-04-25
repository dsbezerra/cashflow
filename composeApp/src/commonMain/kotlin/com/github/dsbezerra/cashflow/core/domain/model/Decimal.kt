package com.github.dsbezerra.cashflow.core.domain.model

expect class Decimal {
    fun toCurrency(): String
    fun toDouble(): Double
}

expect fun String.toDecimal(): Decimal
expect fun Double.toDecimal(): Decimal