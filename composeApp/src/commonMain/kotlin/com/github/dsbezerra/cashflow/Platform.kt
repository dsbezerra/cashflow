package com.github.dsbezerra.cashflow

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform