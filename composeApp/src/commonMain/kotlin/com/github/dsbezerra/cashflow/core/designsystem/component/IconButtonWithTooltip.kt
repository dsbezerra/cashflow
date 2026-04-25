package com.github.dsbezerra.cashflow.core.designsystem.component

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.dsbezerra.cashflow.isDesktop

/**
 * An [IconButton] that shows a [PlainTooltip] on hover when running on Desktop.
 * On Android and iOS the button renders exactly as a plain [IconButton].
 *
 * @param tooltip Text shown in the tooltip on Desktop.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IconButtonWithTooltip(
    onClick: () -> Unit,
    tooltip: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    if (isDesktop) {
        TooltipBox(
            positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
            tooltip = { PlainTooltip { Text(tooltip) } },
            state = rememberTooltipState(),
        ) {
            IconButton(onClick = onClick, modifier = modifier, enabled = enabled, content = content)
        }
    } else {
        IconButton(onClick = onClick, modifier = modifier, enabled = enabled, content = content)
    }
}
