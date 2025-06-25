package top.fatweb.oxygen.toolbox.data.tool

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import top.fatweb.oxygen.toolbox.di.Dispatcher
import top.fatweb.oxygen.toolbox.di.OxygenDispatchers
import javax.inject.Inject

class ToolDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    @Dispatcher(OxygenDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) {
    val toolViewTemplate = flow {
        emit(
            context.assets.open("template/tool-view.html")
                .bufferedReader()
                .use {
                    it.readText()
                }
        )
    }.flowOn(ioDispatcher)

    fun getGlobalJsVariables(isDarkMode: Boolean) = flow {
        emit(
            context.assets.open(
                if (isDarkMode) "template/global-variables-dark.js"
                else "template/global-variables-light.js"
            )
                .bufferedReader()
                .use {
                    it.readText()
                }
        )
    }.flowOn(ioDispatcher)

    fun getGlobalCssVariables(isDarkMode: Boolean) = flow {
        emit(
            context.assets.open(
                if (isDarkMode) "template/global-variables-dark.css"
                else "template/global-variables-light.css"
            )
                .bufferedReader()
                .use {
                    it.readText()
                }
        )
    }.flowOn(ioDispatcher)
}
