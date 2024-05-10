package top.fatweb.oxygen.toolbox.icon

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Preview
@Composable
private fun VectorPreview() {
    Image(OxygenIcons.Loading, null)
}

private var loading: ImageVector? = null

val OxygenIcons.Loading: ImageVector
    get() {
        if (loading != null) {
            return loading!!
        }
        loading = ImageVector.Builder(
            name = "Loading",
            defaultWidth = 1024.dp,
            defaultHeight = 1024.dp,
            viewportWidth = 1024f,
            viewportHeight = 1024f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1.0f,
                stroke = null,
                strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(988f, 548f)
                curveToRelative(-19.9f, 0f, -36f, -16.1f, -36f, -36f)
                curveToRelative(0f, -59.4f, -11.6f, -117f, -34.6f, -171.3f)
                arcToRelative(
                    440.45f,
                    440.45f,
                    0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    -94.3f,
                    -139.9f
                )
                arcToRelative(
                    437.71f,
                    437.71f,
                    0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    -139.9f,
                    -94.3f
                )
                curveTo(629f, 83.6f, 571.4f, 72f, 512f, 72f)
                curveToRelative(-19.9f, 0f, -36f, -16.1f, -36f, -36f)
                reflectiveCurveToRelative(16.1f, -36f, 36f, -36f)
                curveToRelative(69.1f, 0f, 136.2f, 13.5f, 199.3f, 40.3f)
                curveTo(772.3f, 66f, 827f, 103f, 874f, 150f)
                curveToRelative(47f, 47f, 83.9f, 101.8f, 109.7f, 162.7f)
                curveToRelative(26.7f, 63.1f, 40.2f, 130.2f, 40.2f, 199.3f)
                curveToRelative(0.1f, 19.9f, -16f, 36f, -35.9f, 36f)
                close()
            }
        }.build()
        return loading!!
    }
