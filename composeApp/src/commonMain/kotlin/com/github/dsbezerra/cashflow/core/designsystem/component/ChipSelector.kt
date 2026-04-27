package com.github.dsbezerra.cashflow.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/**
 * A flow row of [FilterChip]s for selecting a single item from a list.
 * Intended as a replacement for dropdown menus when the option count is small.
 *
 * @param label Section label displayed above the chip row.
 * @param selectedId The id of the currently selected item, or null for no selection.
 * @param items List of (id, displayName) pairs.
 * @param onSelect Called with the id when the user taps a chip.
 * @param isError Whether to show error styling.
 * @param errorText Error message displayed below the chips when [isError] is true.
 */
@Composable
fun ChipSelector(
    label: String,
    selectedId: String?,
    items: List<Pair<String, String>>,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorText: String? = null,
    leadingIcon: ((String) -> ImageVector)? = null,
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isError) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.onSurfaceVariant,
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(top = 4.dp),
        ) {
            items.forEach { (id, name) ->
                FilterChip(
                    selected = id == selectedId,
                    onClick = { onSelect(id) },
                    label = {
                        Row {
                            if (leadingIcon != null) {
                                Icon(leadingIcon(id), contentDescription = null, modifier = Modifier.size(FilterChipDefaults.IconSize))
                            }
                            if (name.isNotEmpty()) {
                                Text(name)
                            }
                        }
                    }
                )
            }
        }
        if (isError && errorText != null) {
            Text(
                text = errorText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp),
            )
        }
    }
}
