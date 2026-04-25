package com.github.dsbezerra.cashflow.core.domain.usecase.recurring

import com.github.dsbezerra.cashflow.core.domain.model.Frequency
import com.github.dsbezerra.cashflow.core.domain.model.RecurringRule
import com.github.dsbezerra.cashflow.core.domain.model.Transaction
import com.github.dsbezerra.cashflow.core.domain.repository.RecurringRuleRepository
import com.github.dsbezerra.cashflow.core.domain.repository.TransactionRepository
import com.github.dsbezerra.cashflow.util.generateId
import kotlinx.coroutines.flow.first
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlin.time.Instant

class GenerateRecurringTransactionsUseCase(
    private val recurringRuleRepository: RecurringRuleRepository,
    private val transactionRepository: TransactionRepository,
) {
    suspend operator fun invoke(nowMillis: Long) {
        val rules = recurringRuleRepository.getAll().first()
        val dueRules = rules.filter { it.isActive && it.nextOccurrence <= nowMillis }

        for (rule in dueRules) {
            var next = rule.nextOccurrence
            var updated = rule
            while (next <= nowMillis) {
                if (updated.endDate != null && next > updated.endDate) break
                transactionRepository.insert(updated.toTransaction(occurrenceMillis = next, createdAt = nowMillis))
                next = updated.copy(nextOccurrence = next).advanceNextOccurrence()
                updated = updated.copy(nextOccurrence = next)
            }
            recurringRuleRepository.update(updated)
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
