package com.github.dsbezerra.cashflow.core.domain.model

import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

actual class Decimal(private val value: BigDecimal) {
    actual fun toCurrency(): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
        return formatter.format(value)
    }

    actual fun toDouble(): Double = value.toDouble()

    override fun toString(): String = toCurrency()
}

actual fun String.toDecimal(): Decimal =
    Decimal(this.replace(",", ".").toBigDecimal())

actual fun Double.toDecimal(): Decimal = Decimal(toBigDecimal())