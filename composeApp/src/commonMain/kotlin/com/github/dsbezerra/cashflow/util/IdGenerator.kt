package com.github.dsbezerra.cashflow.util

internal fun generateId(): String {
    val chars = "0123456789abcdef"
    val sb = StringBuilder(36)
    repeat(36) { i ->
        when (i) {
            8, 13, 18, 23 -> sb.append('-')
            else -> sb.append(chars[kotlin.random.Random.nextInt(chars.length)])
        }
    }
    return sb.toString()
}
