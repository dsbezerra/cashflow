package com.github.dsbezerra.cashflow.feature.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cashflow.composeapp.generated.resources.Res
import cashflow.composeapp.generated.resources.settings_about
import cashflow.composeapp.generated.resources.settings_about_desc
import cashflow.composeapp.generated.resources.settings_manage_categories
import cashflow.composeapp.generated.resources.settings_manage_categories_desc
import cashflow.composeapp.generated.resources.settings_recurring
import cashflow.composeapp.generated.resources.settings_recurring_desc
import org.jetbrains.compose.resources.stringResource

@Composable
fun SettingsScreen(
    onNavigateToCategoryList: () -> Unit = {},
    onNavigateToRecurringList: () -> Unit = {},
    onNavigateToAbout: () -> Unit = {},
) {
    Surface(
        modifier = Modifier.padding(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.small
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            SettingsRow(
                icon = {
                    Icon(
                        Icons.Default.Category,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                },
                title = stringResource(Res.string.settings_manage_categories),
                subtitle = stringResource(Res.string.settings_manage_categories_desc),
                onClick = onNavigateToCategoryList,
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.surface)
            SettingsRow(
                icon = {
                    Icon(
                        Icons.Default.Repeat,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                },
                title = stringResource(Res.string.settings_recurring),
                subtitle = stringResource(Res.string.settings_recurring_desc),
                onClick = onNavigateToRecurringList,
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.surface)
            SettingsRow(
                icon = {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                },
                title = stringResource(Res.string.settings_about),
                subtitle = stringResource(Res.string.settings_about_desc),
                onClick = onNavigateToAbout,
            )
        }
    }
}

@Composable
private fun SettingsRow(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        icon()
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
        ) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
