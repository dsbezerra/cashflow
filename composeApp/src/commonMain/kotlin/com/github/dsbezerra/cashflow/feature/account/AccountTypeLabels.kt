package com.github.dsbezerra.cashflow.feature.account

import androidx.compose.runtime.Composable
import cashflow.composeapp.generated.resources.Res
import cashflow.composeapp.generated.resources.account_type_cash
import cashflow.composeapp.generated.resources.account_type_checking
import cashflow.composeapp.generated.resources.account_type_credit_card
import cashflow.composeapp.generated.resources.account_type_investments
import cashflow.composeapp.generated.resources.account_type_savings
import com.github.dsbezerra.cashflow.core.domain.model.AccountType
import org.jetbrains.compose.resources.stringResource

@Composable
fun accountTypeName(type: AccountType): String = when (type) {
    AccountType.CHECKING -> stringResource(Res.string.account_type_checking)
    AccountType.SAVINGS -> stringResource(Res.string.account_type_savings)
    AccountType.CASH -> stringResource(Res.string.account_type_cash)
    AccountType.CREDIT -> stringResource(Res.string.account_type_credit_card)
    AccountType.INVESTMENT -> stringResource(Res.string.account_type_investments)
}
