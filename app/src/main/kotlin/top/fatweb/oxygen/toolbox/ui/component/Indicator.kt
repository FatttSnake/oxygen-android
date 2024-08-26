package top.fatweb.oxygen.toolbox.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun Indicator(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    contentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CompositionLocalProvider(value = LocalContentColor provides contentColor) {
            Box(
                modifier = modifier
                    .size(SpinnerContainerSize)
                    .shadow(
                        elevation = Elevation,
                        shape = CircleShape,
                        clip = true
                    )
                    .background(color = containerColor, shape = CircleShape)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(SpinnerSize),
                    strokeWidth = StrokeWidth,
                    color = LocalContentColor.current
                )
            }
        }
    }
}

private val SpinnerContainerSize = 40.dp
private val Elevation = 3.dp
private val StrokeWidth = 2.5.dp
private val SpinnerSize = 16.dp
