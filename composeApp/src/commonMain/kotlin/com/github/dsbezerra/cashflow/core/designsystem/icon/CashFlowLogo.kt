import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathData
import androidx.compose.ui.graphics.vector.group
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
            group(
                clipPathData = PathData {
                    moveTo(0f, 0f)
                    horizontalLineToRelative(512f)
                    verticalLineToRelative(512f)
                    horizontalLineToRelative(-512f)
                    close()
                }
            ) {
                path(fill = SolidColor(Color(0xFF006C47))) {
                    moveTo(412f, 0f)
                    horizontalLineTo(100f)
                    curveTo(44.77f, 0f, 0f, 44.77f, 0f, 100f)
                    verticalLineTo(412f)
                    curveTo(0f, 467.23f, 44.77f, 512f, 100f, 512f)
                    horizontalLineTo(412f)
                    curveTo(467.23f, 512f, 512f, 467.23f, 512f, 412f)
                    verticalLineTo(100f)
                    curveTo(512f, 44.77f, 467.23f, 0f, 412f, 0f)
                    close()
                }
                path(
                    stroke = SolidColor(Color.White),
                    strokeLineWidth = 36f,
                    strokeLineCap = StrokeCap.Round
                ) {
                    moveTo(397f, 307f)
                    curveTo(386.49f, 335.96f, 367.32f, 360.98f, 342.09f, 378.66f)
                    curveTo(316.86f, 396.34f, 286.81f, 405.82f, 256f, 405.82f)
                    curveTo(225.19f, 405.82f, 195.13f, 396.34f, 169.91f, 378.66f)
                    curveTo(144.68f, 360.98f, 125.51f, 335.96f, 115f, 307f)
                }
                path(fill = SolidColor(Color.White)) {
                    moveTo(92.92f, 258.39f)
                    lineTo(147.98f, 300.44f)
                    lineTo(84.04f, 327.1f)
                    lineTo(92.92f, 258.39f)
                    close()
                }
                path(
                    stroke = SolidColor(Color(0xFF8EF8C3)),
                    strokeLineWidth = 36f,
                    strokeLineCap = StrokeCap.Round
                ) {
                    moveTo(115f, 205f)
                    curveTo(125.51f, 176.04f, 144.68f, 151.02f, 169.91f, 133.34f)
                    curveTo(195.13f, 115.66f, 225.19f, 106.18f, 256f, 106.18f)
                    curveTo(286.81f, 106.18f, 316.86f, 115.66f, 342.09f, 133.34f)
                    curveTo(367.32f, 151.02f, 386.49f, 176.04f, 397f, 205f)
                }
                path(fill = SolidColor(Color(0xFF8EF8C3))) {
                    moveTo(414.12f, 245.36f)
                    lineTo(358.04f, 204.67f)
                    lineTo(421.32f, 176.45f)
                    lineTo(414.12f, 245.36f)
                    close()
                }
            }
        }.build()

        return _CashflowLogo!!
    }

@Suppress("ObjectPropertyName")
private var _CashflowLogo: ImageVector? = null
