package top.fatweb.oxygen.toolbox.ui.component

import androidx.annotation.StringRes
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import top.fatweb.oxygen.toolbox.ui.util.ResourcesUtils

@Composable
fun ClickableText(
    @StringRes text: Int,
    @StringRes replaceText: Int,
    onClick: (Int) -> Unit
    ) {
    val context = LocalContext.current
    val primaryColor = MaterialTheme.colorScheme.primary

    val annotatedString = buildAnnotatedString {
        val clickablePart = ResourcesUtils.getString(
            context = context,
            resId = replaceText
        )
        val mainText = ResourcesUtils.getString(
            context = context,
            resId = text,
            clickablePart
        )
        append(mainText.substringBefore(clickablePart))
        pushStringAnnotation(tag = "Click", annotation = clickablePart)
        withStyle(style = SpanStyle(color = primaryColor)) {
            append(clickablePart)
        }
        pop()
        append(mainText.substringAfter(clickablePart))
    }

    ClickableText(text = annotatedString, onClick = onClick)
}