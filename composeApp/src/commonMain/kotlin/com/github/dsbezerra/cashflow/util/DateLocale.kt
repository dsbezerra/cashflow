package com.github.dsbezerra.cashflow.util

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month

fun Month.namePtBr(): String = when (this) {
    Month.JANUARY -> "janeiro"
    Month.FEBRUARY -> "fevereiro"
    Month.MARCH -> "março"
    Month.APRIL -> "abril"
    Month.MAY -> "maio"
    Month.JUNE -> "junho"
    Month.JULY -> "julho"
    Month.AUGUST -> "agosto"
    Month.SEPTEMBER -> "setembro"
    Month.OCTOBER -> "outubro"
    Month.NOVEMBER -> "novembro"
    Month.DECEMBER -> "dezembro"
    else -> name.lowercase()
}

fun DayOfWeek.namePtBr(): String = when (this) {
    DayOfWeek.MONDAY -> "Segunda-feira"
    DayOfWeek.TUESDAY -> "Terça-feira"
    DayOfWeek.WEDNESDAY -> "Quarta-feira"
    DayOfWeek.THURSDAY -> "Quinta-feira"
    DayOfWeek.FRIDAY -> "Sexta-feira"
    DayOfWeek.SATURDAY -> "Sábado"
    DayOfWeek.SUNDAY -> "Domingo"
    else -> name.lowercase()
}

fun LocalDate.formatPtBr(): String = "$day de ${month.namePtBr()} de $year"

fun LocalDate.formatFullPtBr(): String = "${dayOfWeek.namePtBr()}, $day de ${month.namePtBr()} de $year"

expect fun formatLongDate(timestamp: Long, pattern: String): String
