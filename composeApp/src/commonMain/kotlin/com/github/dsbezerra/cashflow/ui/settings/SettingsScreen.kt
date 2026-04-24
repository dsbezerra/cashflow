package com.github.dsbezerra.cashflow.ui.settings

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
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    onNavigateToCategoryList: () -> Unit = {},
    onNavigateToRecurringList: () -> Unit = {},
) {
    Column(modifier = Modifier.fillMaxSize()) {
        SettingsRow(
            icon = { Icon(Icons.Default.Category, contentDescription = null, modifier = Modifier.size(24.dp)) },
            title = "Gerenciar Categorias",
            subtitle = "Criar, editar e arquivar categorias",
            onClick = onNavigateToCategoryList,
        )
        HorizontalDivider(modifier = Modifier.padding(start = 56.dp))
        SettingsRow(
            icon = { Icon(Icons.Default.Repeat, contentDescription = null, modifier = Modifier.size(24.dp)) },
            title = "Regras Recorrentes",
            subtitle = "Gerenciar transações automáticas",
            onClick = onNavigateToRecurringList,
        )
        HorizontalDivider(modifier = Modifier.padding(start = 56.dp))
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
