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
    Image(Loading, null)
}

val Loading: ImageVector
    get() = ImageVector.Builder(
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
            moveTo(x = 988f, y = 548f)
            curveToRelative(
                dx1 = -19.9f,
                dy1 = 0f,
                dx2 = -36f,
                dy2 = -16.1f,
                dx3 = -36f,
                dy3 = -36f
            )
            curveToRelative(
                dx1 = 0f,
                dy1 = -59.4f,
                dx2 = -11.6f,
                dy2 = -117f,
                dx3 = -34.6f,
                dy3 = -171.3f
            )
            arcToRelative(
                a = 440.45f,
                b = 440.45f,
                theta = 0f,
                isMoreThanHalf = false,
                isPositiveArc = false,
                dx1 = -94.3f,
                dy1 = -139.9f
            )
            arcToRelative(
                a = 437.71f,
                b = 437.71f,
                theta = 0f,
                isMoreThanHalf = false,
                isPositiveArc = false,
                dx1 = -139.9f,
                dy1 = -94.3f
            )
            curveTo(x1 = 629f, y1 = 83.6f, x2 = 571.4f, y2 = 72f, x3 = 512f, y3 = 72f)
            curveToRelative(
                dx1 = -19.9f,
                dy1 = 0f,
                dx2 = -36f,
                dy2 = -16.1f,
                dx3 = -36f,
                dy3 = -36f
            )
            reflectiveCurveToRelative(dx1 = 16.1f, dy1 = -36f, dx2 = 36f, dy2 = -36f)
            curveToRelative(
                dx1 = 69.1f,
                dy1 = 0f,
                dx2 = 136.2f,
                dy2 = 13.5f,
                dx3 = 199.3f,
                dy3 = 40.3f
            )
            curveTo(x1 = 772.3f, y1 = 66f, x2 = 827f, y2 = 103f, x3 = 874f, y3 = 150f)
            curveToRelative(
                dx1 = 47f,
                dy1 = 47f,
                dx2 = 83.9f,
                dy2 = 101.8f,
                dx3 = 109.7f,
                dy3 = 162.7f
            )
            curveToRelative(
                dx1 = 26.7f,
                dy1 = 63.1f,
                dx2 = 40.2f,
                dy2 = 130.2f,
                dx3 = 40.2f,
                dy3 = 199.3f
            )
            curveToRelative(dx1 = 0.1f, dy1 = 19.9f, dx2 = -16f, dy2 = 36f, dx3 = -35.9f, dy3 = 36f)
            close()
        }
    }.build()
