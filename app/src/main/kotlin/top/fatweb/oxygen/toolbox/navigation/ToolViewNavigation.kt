package top.fatweb.oxygen.toolbox.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import top.fatweb.oxygen.toolbox.ui.view.ToolViewRoute
import java.net.URLDecoder
import java.net.URLEncoder
import kotlin.text.Charsets.UTF_8

private val URL_CHARACTER_ENCODING = UTF_8.name()

internal const val USER_NAME_ARG = "username"
internal const val TOOL_ID_ARG = "toolId"
internal const val PREVIEW_ARG = "preview"
const val TOOL_VIEW_ROUTE = "tool_view_route"

internal class ToolViewArgs(
    val username: String,
    val toolId: String,
    val preview: Boolean
) {
    constructor(savedStateHandle: SavedStateHandle) :
            this(
                URLDecoder.decode(
                    checkNotNull(savedStateHandle[USER_NAME_ARG]),
                    URL_CHARACTER_ENCODING
                ),
                URLDecoder.decode(
                    checkNotNull(savedStateHandle[TOOL_ID_ARG]),
                    URL_CHARACTER_ENCODING
                ),
                checkNotNull(savedStateHandle[PREVIEW_ARG])
            )
}

fun NavController.navigateToToolView(
    username: String,
    toolId: String,
    preview: Boolean,
    navOptions: NavOptionsBuilder.() -> Unit = {}
) {
    val encodedUsername = URLEncoder.encode(username, URL_CHARACTER_ENCODING)
    val encodedToolId = URLEncoder.encode(toolId, URL_CHARACTER_ENCODING)
    val newRoute = "$TOOL_VIEW_ROUTE/$encodedUsername/$encodedToolId?$PREVIEW_ARG=$preview"
    navigate(newRoute) {
        navOptions()
    }
}

fun NavGraphBuilder.toolViewScreen(
    onBackClick: () -> Unit
) {
    composable(
        route = "${TOOL_VIEW_ROUTE}/{$USER_NAME_ARG}/{$TOOL_ID_ARG}?$PREVIEW_ARG={$PREVIEW_ARG}",
        arguments = listOf(
            navArgument(USER_NAME_ARG) { type = NavType.StringType },
            navArgument(TOOL_ID_ARG) { type = NavType.StringType },
            navArgument(PREVIEW_ARG) { type = NavType.BoolType; defaultValue = false }
        )
    ) {
        ToolViewRoute(
            onBackClick = onBackClick
        )
    }
}
