package com.github.dsbezerra.cashflow.core.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val CashflowLogo: ImageVector
    get() {
        if (_CashflowLogo != null) {
            return _CashflowLogo!!
        }
        _CashflowLogo = ImageVector.Builder(
            name = "CashflowLogo",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 512f,
            viewportHeight = 512f
        ).apply {
            path(fill = SolidColor(Color(0xFF006C47))) {
                moveTo(100f, 0f)
                lineTo(412f, 0f)
                arcTo(100f, 100f, 0f, isMoreThanHalf = false, isPositiveArc = true, 512f, 100f)
                lineTo(512f, 412f)
                arcTo(100f, 100f, 0f, isMoreThanHalf = false, isPositiveArc = true, 412f, 512f)
                lineTo(100f, 512f)
                arcTo(100f, 100f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0f, 412f)
                lineTo(0f, 100f)
                arcTo(100f, 100f, 0f, isMoreThanHalf = false, isPositiveArc = true, 100f, 0f)
                close()
            }
            path(
                stroke = SolidColor(Color(0xFF8EF8C3)),
                strokeLineWidth = 36f,
                strokeLineCap = StrokeCap.Round
            ) {
                moveTo(115f, 205f)
                arcTo(150f, 150f, 0f, isMoreThanHalf = false, isPositiveArc = true, 397f, 205f)
            }
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 36f,
                strokeLineCap = StrokeCap.Round
            ) {
                moveTo(397f, 307f)
                arcTo(150f, 150f, 0f, isMoreThanHalf = false, isPositiveArc = true, 115f, 307f)
            }
        }.build()

        return _CashflowLogo!!
    }

@Suppress("ObjectPropertyName")
private var _CashflowLogo: ImageVector? = null
