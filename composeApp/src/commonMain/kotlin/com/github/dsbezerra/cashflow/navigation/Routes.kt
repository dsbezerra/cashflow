package com.github.dsbezerra.cashflow.navigation

import kotlinx.serialization.Serializable

@Serializable
data object Dashboard

@Serializable
data object TransactionList

@Serializable
data class TransactionDetail(
    val transactionId: String? = null,
    val defaultAccountId: String? = null,
    val defaultType: String? = null,
)

@Serializable
data object Accounts

@Serializable
data class AccountDetail(val accountId: String)

@Serializable
data class AccountForm(val accountId: String? = null)

@Serializable
data object Settings

@Serializable
data object CategoryList

@Serializable
data class CategoryForm(val categoryId: String? = null)

@Serializable
data object Reports
