package com.github.dsbezerra.cashflow.domain.model

data class Transaction(
    val id: String,
    val accountId: String,
    val categoryId: String,
    val type: TransactionType,
    val amount: Double,
    val description: String,
    val date: Long,
    val notes: String?,
    val attachmentPath: String?,
    val isRecurring: Boolean,
    val recurringId: String?,
    val createdAt: Long,
    val updatedAt: Long,
)
