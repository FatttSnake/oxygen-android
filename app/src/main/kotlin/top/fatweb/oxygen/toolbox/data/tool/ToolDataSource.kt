package top.fatweb.oxygen.toolbox.data.tool

import kotlinx.coroutines.flow.flowOf
import top.fatweb.oxygen.toolbox.icon.OxygenIcons
import top.fatweb.oxygen.toolbox.model.tool.Tool
import top.fatweb.oxygen.toolbox.model.tool.ToolGroup
import javax.inject.Inject
import kotlin.random.Random

class ToolDataSource @Inject constructor() {
    val tool = flowOf(
        (0..100).map { index ->
            ToolGroup(
                id = "local-base-$index",
                title = "${generateRandomString()}-$index",
                icon = OxygenIcons.Tool,
                tools = (0..20).map {
                    Tool(
                        id = "local-base-$index-time-screen-$it",
                        icon = OxygenIcons.Time,
                        name = "${generateRandomString()}-$index-$it"
                    )
                }
            )
        }
    )

    private fun generateRandomString(length: Int = (1..10).random()): String {
        val words = ('a'..'z') + ('A'..'Z')

        return (1..length)
            .map { Random.nextInt(0, words.size) }
            .map(words::get)
            .joinToString("")
    }
}