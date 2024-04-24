package top.fatweb.oxygen.toolbox.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import top.fatweb.oxygen.toolbox.data.tool.ToolDataSource
import top.fatweb.oxygen.toolbox.icon.OxygenIcons
import top.fatweb.oxygen.toolbox.model.tool.Tool
import top.fatweb.oxygen.toolbox.model.tool.ToolGroup
import top.fatweb.oxygen.toolbox.ui.theme.OxygenPreviews
import top.fatweb.oxygen.toolbox.ui.theme.OxygenTheme

@Composable
fun ToolGroupCard(
    modifier: Modifier = Modifier,
    toolGroup: ToolGroup
) {
    val (_, icon, title, tools) = toolGroup

    var isExpanded by remember { mutableStateOf(true) }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            ToolGroupTitle(
                modifier = Modifier.padding(16.dp),
                icon = icon,
                title = title,
                isExpanded = isExpanded,
                onClick = { isExpanded = !isExpanded }
            )
            AnimatedVisibility(visible = isExpanded) {
                ToolGroupContent(modifier = Modifier.padding(16.dp), toolList = tools)
            }
        }
    }
}

@Composable
fun ToolGroupTitle(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    isExpanded: Boolean,
    onClick: (() -> Unit)? = null
) {
    Surface(onClick = onClick ?: {}) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(modifier = Modifier.size(18.dp), imageVector = icon, contentDescription = title)
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.weight(1f))
            SwitchableIcon(icon = OxygenIcons.ArrowDown, switched = !isExpanded)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ToolGroupContent(
    modifier: Modifier = Modifier,
    toolList: List<Tool>
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        toolList.map {
            ToolGroupItem(icon = it.icon, title = it.name)
        }
    }
}

@Composable
fun ToolGroupItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(100),
        onClick = onClick ?: { }
    ) {
        Box(
            modifier = Modifier.padding(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(modifier = Modifier.size(16.dp), imageVector = icon, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = title, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun SwitchableIcon(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    switched: Boolean,
    defaultRotate: Float = 0f,
    switchedRotate: Float = 180f,
) {
    val rotate by animateFloatAsState(
        if (switched) switchedRotate else defaultRotate,
        label = "Rotate"
    )

    Icon(
        modifier = modifier
            .rotate(rotate), imageVector = icon, contentDescription = null
    )
}

@OxygenPreviews
@Composable
private fun ToolGroupCardPreview() {
    val groups = runBlocking { ToolDataSource().tool.first() }

    OxygenTheme {
        LazyColumn {
            itemsIndexed(groups) { index, item ->
                if (index != 0) {
                    Spacer(modifier = Modifier.height(10.dp))
                }
                ToolGroupCard(
                    toolGroup = item
                )
            }
        }
    }
}

@OxygenPreviews
@Composable
fun SwitchableIconPreview() {
    var switched by remember { mutableStateOf(false) }

    OxygenTheme {
        Surface(
            onClick = { switched = !switched }
        ) {
            SwitchableIcon(icon = OxygenIcons.ArrowDown, switched = switched)
        }
    }
}

@OxygenPreviews
@Composable
fun ToolGroupItemPreview() {
    OxygenTheme {
        ToolGroupItem(icon = OxygenIcons.Time, title = "Time Screen")
    }
}

@OxygenPreviews
@Composable
fun ToolGroupContentPreview() {
    OxygenTheme {
        ToolGroupContent(toolList = runBlocking {
            ToolDataSource().tool.first().map { it.tools }.flatten()
        })
    }
}