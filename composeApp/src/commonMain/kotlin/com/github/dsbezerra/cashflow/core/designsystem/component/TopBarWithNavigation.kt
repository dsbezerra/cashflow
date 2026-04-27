package com.github.dsbezerra.cashflow.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cashflow.composeapp.generated.resources.Res
import cashflow.composeapp.generated.resources.topbar_next
import cashflow.composeapp.generated.resources.topbar_previous
import com.github.dsbezerra.cashflow.core.designsystem.theme.CashFlowTheme
import com.github.dsbezerra.cashflow.isDesktop
import org.jetbrains.compose.resources.stringResource

@Composable
fun TopBarWithNavigation(
    title: @Composable () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TopBarActionButton(
            imageVector = Icons.AutoMirrored.Default.ArrowBack,
            contentDescription = stringResource(Res.string.topbar_previous),
            onClick = onPrevious
        )
        title()
        TopBarActionButton(
            imageVector = Icons.AutoMirrored.Default.ArrowForward,
            contentDescription = stringResource(Res.string.topbar_next),
            onClick = onNext
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TopBarActionButton(
    imageVector: ImageVector,
    contentDescription: String? = null,
    onClick: () -> Unit
) {
    val button: @Composable () -> Unit = {
        FilledIconButton(
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
            onClick = onClick
        ) {
            Icon(
                imageVector = imageVector,
                contentDescription = contentDescription
            )
        }
    }
    if (isDesktop && contentDescription != null) {
        TooltipBox(
            positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
            tooltip = { PlainTooltip { Text(contentDescription) } },
            state = rememberTooltipState(),
        ) { button() }
    } else {
        button()
    }
}

@Preview
@Composable
private fun PreviewTopBarWithNavigation() {
    CashFlowTheme {
        TopBarWithNavigation(
            title = {
                Text(
                    text = "August 2024",
                    style = MaterialTheme.typography.titleMedium
                )
            },
            onPrevious = {},
            onNext = {}
        )
    }
}
