package top.fatweb.oxygen.toolbox.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import top.fatweb.oxygen.toolbox.ui.tool.ToolViewRoute
import java.net.URLDecoder
import java.net.URLEncoder
import kotlin.text.Charsets.UTF_8

private val URL_CHARACTER_ENCODING = UTF_8.name()

internal const val USER_NAME_ARG = "username"
internal const val TOOL_ID_ARG = "toolId"
const val TOOL_VIEW_ROUTE = "tool_view_route"

internal class ToolViewArgs(val username: String, val toolId: String) {
    constructor(savedStateHandle: SavedStateHandle) :
            this(
                URLDecoder.decode(
                    checkNotNull(savedStateHandle[USER_NAME_ARG]),
                    URL_CHARACTER_ENCODING
                ),
                URLDecoder.decode(
                    checkNotNull(savedStateHandle[TOOL_ID_ARG]),
                    URL_CHARACTER_ENCODING
                )
            )
}

fun NavController.navigateToToolView(
    username: String,
    toolId: String,
    navOptions: NavOptionsBuilder.() -> Unit = {}
) {
    val encodedUsername = URLEncoder.encode(username, URL_CHARACTER_ENCODING)
    val encodedToolId = URLEncoder.encode(toolId, URL_CHARACTER_ENCODING)
    val newRoute = "$TOOL_VIEW_ROUTE/$encodedUsername/$encodedToolId"
    navigate(newRoute) {
        navOptions()
    }
}

fun NavGraphBuilder.toolViewScreen(
    onBackClick: () -> Unit
) {
    composable(
        route = "${TOOL_VIEW_ROUTE}/{$USER_NAME_ARG}/{$TOOL_ID_ARG}",
        arguments = listOf(
            navArgument(USER_NAME_ARG) { type = NavType.StringType },
            navArgument(TOOL_ID_ARG) { type = NavType.StringType }
        )
    ) {
        ToolViewRoute(
            onBackClick = onBackClick
        )
    }
}
