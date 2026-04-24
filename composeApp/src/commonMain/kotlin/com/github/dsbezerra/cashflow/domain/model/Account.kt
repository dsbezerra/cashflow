package com.github.dsbezerra.cashflow.domain.model

data class Account(
    val id: String,
    val name: String,
    val type: AccountType,
    val currency: String,
    val initialBalance: Decimal,
    val color: String,
    val icon: String,
    val isArchived: Boolean,
    val isDefault: Boolean = false,
    val createdAt: Long,
)
