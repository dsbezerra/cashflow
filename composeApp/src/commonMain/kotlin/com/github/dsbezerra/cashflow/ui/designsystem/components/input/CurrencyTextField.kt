package com.github.dsbezerra.cashflow.ui.designsystem.components.input

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue

/** Maximum number of digits allowed (prevents Long overflow). 13 digits = R$ 99.999.999.999,99 */
private const val MAX_DIGITS = 13

/**
 * Formats a Long (cents) as Brazilian Real currency string.
 * Pure-Kotlin — no JVM/platform APIs.
 *
 * Examples:
 *   0       → "R$ 0,00"
 *   1234    → "R$ 12,34"
 *   123456  → "R$ 1.234,56"
 */
internal fun formatBRL(cents: Long): String {
    val reais = cents / 100
    val centavos = cents % 100
    val reaisStr = reais.toString()
    val withThousands = buildString {
        reaisStr.forEachIndexed { i, c ->
            if (i > 0 && (reaisStr.length - i) % 3 == 0) append('.')
            append(c)
        }
    }
    return "R$ $withThousands,${centavos.toString().padStart(2, '0')}"
}

/**
 * A Material 3 text field that accepts numeric input and displays it formatted as BRL
 * (Brazilian Real) currency in real-time.
 *
 * Input UX: digits accumulate from the right (ATM-style). Backspace removes the last digit.
 * The cursor is always kept at the end of the text — this is intentional and prevents
 * unexpected mid-string cursor jumps when the formatted text length changes.
 *
 * @param value Current amount in cents (e.g., 1234 = R$ 12,34).
 * @param onValueChange Called with the new amount in cents on every keystroke.
 */
@Composable
fun CurrencyTextField(
    value: Long,
    onValueChange: (Long) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    isError: Boolean = false,
    supportingText: (@Composable () -> Unit)? = null,
) {
    val initial = formatBRL(value)
    var tfv by remember {
        mutableStateOf(TextFieldValue(text = initial, selection = TextRange(initial.length)))
    }

    // Sync when an external value change arrives (e.g., loading an existing transaction).
    LaunchedEffect(value) {
        val formatted = formatBRL(value)
        if (tfv.text != formatted) {
            tfv = TextFieldValue(text = formatted, selection = TextRange(formatted.length))
        }
    }

    OutlinedTextField(
        value = tfv,
        onValueChange = { newTfv ->
            val digits = newTfv.text.filter { it.isDigit() }.take(MAX_DIGITS)
            val cents = if (digits.isEmpty()) 0L else digits.toLongOrNull() ?: return@OutlinedTextField
            val formatted = formatBRL(cents)
            tfv = TextFieldValue(text = formatted, selection = TextRange(formatted.length))
            onValueChange(cents)
        },
        modifier = modifier,
        label = label?.let { { Text(it) } },
        isError = isError,
        supportingText = supportingText,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
    )
}
