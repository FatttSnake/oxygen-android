package top.fatweb.oxygen.toolbox.ui.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.LocalShimmerTheme
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer
import com.valentinilk.shimmer.shimmerSpec
import top.fatweb.oxygen.toolbox.R
import top.fatweb.oxygen.toolbox.icon.OxygenIcons
import top.fatweb.oxygen.toolbox.model.tool.ToolEntity

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ToolCard(
    modifier: Modifier = Modifier,
    tool: ToolEntity,
    specifyVer: String? = null,
    actionIcon: ImageVector? = null,
    actionIconContentDescription: String = "",
    onAction: () -> Unit = {},
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            ToolHeader(
                ver = specifyVer ?: tool.ver,
                actionIcon = actionIcon,
                actionIconContentDescription = actionIconContentDescription,
                onAction = onAction
            )
            Spacer(Modifier.height(16.dp))
            ToolIcon(icon = tool.icon)
            Spacer(Modifier.height(16.dp))
            ToolInfo(
                toolName = tool.name,
                toolId = tool.toolId,
                toolDesc = tool.description
            )
            Spacer(Modifier.height(16.dp))
            AuthorInfo(
                avatar = tool.authorAvatar,
                nickname = tool.authorNickname
            )
        }
    }
}

@Composable
private fun ToolHeader(
    modifier: Modifier = Modifier,
    ver: String,
    actionIcon: ImageVector?,
    actionIconContentDescription: String,
    onAction: () -> Unit
) {
    Row(
        modifier = modifier
            .height(28.dp)
    ) {
        ToolVer(ver = ver)
        Spacer(Modifier.weight(1f))
        actionIcon?.let {
            ToolAction(
                actionIcon = actionIcon,
                actionIconContentDescription = actionIconContentDescription,
                onAction = onAction
            )
        }
    }
}

@Composable
private fun ToolVer(
    modifier: Modifier = Modifier,
    ver: String
) {
    Card(
        modifier = modifier
            .fillMaxHeight(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .background(color = MaterialTheme.colorScheme.surfaceContainer)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            Arrangement.Center,
            Alignment.CenterHorizontally
        ) {
            Text(
                style = MaterialTheme.typography.bodyMedium,
                text = ver
            )
        }
    }
}

@Composable
private fun ToolAction(
    modifier: Modifier = Modifier,
    actionIcon: ImageVector,
    actionIconContentDescription: String,
    onAction: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(8.dp))
            .clickable(
                onClick = onAction
            ),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .background(color = MaterialTheme.colorScheme.surfaceContainer)
                .padding(horizontal = 6.dp, vertical = 6.dp)
        ) {
            Icon(
                modifier = Modifier,
                imageVector = actionIcon,
                contentDescription = actionIconContentDescription
            )
        }
    }
}

@Composable
private fun ToolIcon(
    modifier: Modifier = Modifier,
    icon: String
) {
    Box(
        modifier = modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier.size(80.dp),
            bitmap = OxygenIcons.fromSvgBase64(icon),
            contentDescription = ""
        )
    }
}

@Composable
private fun ToolInfo(
    modifier: Modifier = Modifier,
    toolName: String,
    toolId: String,
    toolDesc: String?
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            text = toolName
        )
        Text(
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            text = "ID: $toolId"
        )
        toolDesc?.let {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline,
                text = "${stringResource(R.string.feature_tools_description)}: $it"
            )
        }
    }
}

@Composable
private fun AuthorInfo(
    modifier: Modifier = Modifier,
    avatar: String,
    nickname: String
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
    ) {
        Surface(
            modifier = Modifier
                .size(24.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Image(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceContainer),
                bitmap = OxygenIcons.fromPngBase64(avatar), contentDescription = "Avatar"
            )
        }
        Text(
            style = MaterialTheme.typography.bodyMedium,
            text = nickname
        )
    }
}

@Composable
fun ToolCardSkeleton(
    modifier: Modifier = Modifier
) {
    val shimmer = rememberShimmer(
        shimmerBounds = ShimmerBounds.Window,
        theme = LocalShimmerTheme.current.copy(
            animationSpec = infiniteRepeatable(
                animation = shimmerSpec(
                    durationMillis = 1_500,
                    easing = LinearEasing,
                    delayMillis = 200
                ),
                repeatMode = RepeatMode.Restart
            )
        )
    )

    Card(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .height(28.dp)
            ) {
                Surface(
                    modifier = modifier
                        .fillMaxHeight()
                        .width(64.dp)
                        .shimmer(shimmer),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceContainer
                ) {}
            }
            Spacer(Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier
                        .size(80.dp)
                        .shimmer(shimmer),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceContainer
                ) {}
            }
            Spacer(Modifier.height(16.dp))
            Column(
                modifier = modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .size(width = 40.dp, height = 22.dp)
                        .shimmer(shimmer),
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.surfaceContainer
                ) {}
                Surface(
                    modifier = Modifier
                        .size(width = 60.dp, height = 20.dp)
                        .shimmer(shimmer),
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.surfaceContainer
                ) {}
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .size(width = 120.dp, height = 12.dp)
                            .shimmer(shimmer),
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.surfaceContainer
                    ) {}
                    Surface(
                        modifier = Modifier
                            .size(width = 120.dp, height = 12.dp)
                            .shimmer(shimmer),
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.surfaceContainer
                    ) {}
                    Surface(
                        modifier = Modifier
                            .size(width = 80.dp, height = 12.dp)
                            .shimmer(shimmer),
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.surfaceContainer
                    ) {}
                }
            }
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
            ) {
                Surface(
                    modifier = Modifier
                        .size(24.dp)
                        .shimmer(shimmer),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceContainer
                ) {}
                Surface(
                    modifier = Modifier
                        .size(width = 120.dp, height = 12.dp)
                        .shimmer(shimmer),
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.surfaceContainer
                ) {}
            }
        }
    }
}

const val DEFAULT_TOOL_CARD_SKELETON_COUNT = 20
