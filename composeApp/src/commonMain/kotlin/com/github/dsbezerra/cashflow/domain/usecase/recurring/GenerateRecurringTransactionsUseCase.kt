package com.github.dsbezerra.cashflow.domain.usecase.recurring

import com.github.dsbezerra.cashflow.domain.model.Frequency
import com.github.dsbezerra.cashflow.domain.model.RecurringRule
import com.github.dsbezerra.cashflow.domain.model.Transaction
import com.github.dsbezerra.cashflow.domain.repository.RecurringRuleRepository
import com.github.dsbezerra.cashflow.domain.repository.TransactionRepository
import com.github.dsbezerra.cashflow.util.generateId
import kotlinx.coroutines.flow.first
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus

class GenerateRecurringTransactionsUseCase(
    private val recurringRuleRepository: RecurringRuleRepository,
    private val transactionRepository: TransactionRepository,
) {
    suspend operator fun invoke(nowMillis: Long) {
        val rules = recurringRuleRepository.getAll().first()
        val dueRules = rules.filter { it.isActive && it.nextOccurrence <= nowMillis }

        for (rule in dueRules) {
            val tx = rule.toTransaction(occurrenceMillis = rule.nextOccurrence, createdAt = nowMillis)
            transactionRepository.insert(tx)

            val advanced = rule.copy(nextOccurrence = rule.advanceNextOccurrence())
            recurringRuleRepository.update(advanced)
        }
    }

    private fun RecurringRule.toTransaction(occurrenceMillis: Long, createdAt: Long) = Transaction(
        id = generateId(),
        accountId = accountId,
        categoryId = categoryId,
        type = type,
        amount = amount,
        description = description,
        date = occurrenceMillis,
        notes = null,
        attachmentPath = null,
        isRecurring = true,
        recurringId = id,
        createdAt = createdAt,
        updatedAt = createdAt,
    )

    private fun RecurringRule.advanceNextOccurrence(): Long {
        val tz = TimeZone.currentSystemDefault()
        val instant = Instant.fromEpochMilliseconds(nextOccurrence)
        val advanced = when (frequency) {
            Frequency.DAILY -> instant.plus(interval, DateTimeUnit.DAY, tz)
            Frequency.WEEKLY -> instant.plus(interval * 7, DateTimeUnit.DAY, tz)
            Frequency.MONTHLY -> instant.plus(interval, DateTimeUnit.MONTH, tz)
            Frequency.YEARLY -> instant.plus(interval, DateTimeUnit.YEAR, tz)
        }
        return advanced.toEpochMilliseconds()
    }
}
