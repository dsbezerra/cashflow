package com.github.dsbezerra.cashflow.core.designsystem.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.Dp

enum class GroupedItemPosition { Top, Middle, Bottom, Single }

fun groupedItemPosition(index: Int, total: Int): GroupedItemPosition = when {
    total == 1 -> GroupedItemPosition.Single
    index == 0 -> GroupedItemPosition.Top
    index == total - 1 -> GroupedItemPosition.Bottom
    else -> GroupedItemPosition.Middle
}

@Composable
fun groupedItemShape(position: GroupedItemPosition, cornerRadius: Dp = 12.dp): Shape {
    val r = cornerRadius
    return when (position) {
        GroupedItemPosition.Single -> RoundedCornerShape(r)
        GroupedItemPosition.Top -> RoundedCornerShape(topStart = r, topEnd = r, bottomStart = 0.dp, bottomEnd = 0.dp)
        GroupedItemPosition.Middle -> RectangleShape
        GroupedItemPosition.Bottom -> RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = r, bottomEnd = r)
    }
}

@Composable
fun GroupedListItem(
    position: GroupedItemPosition,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerLow,
    content: @Composable () -> Unit,
) {
    val showDivider = position == GroupedItemPosition.Top || position == GroupedItemPosition.Middle
    Surface(
        shape = groupedItemShape(position),
        color = containerColor,
        modifier = modifier,
    ) {
        Column {
            content()
            if (showDivider) {
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.surface,
                    thickness = 1.dp,
                )
            }
        }
    }
}

fun <T> LazyListScope.groupedItems(
    items: List<T>,
    key: ((T) -> Any)? = null,
    itemContent: @Composable LazyItemScope.(item: T, position: GroupedItemPosition) -> Unit,
) {
    itemsIndexed(
        items = items,
        key = if (key != null) { _, item -> key(item) } else null,
    ) { index, item ->
        itemContent(item, groupedItemPosition(index, items.size))
    }
}
