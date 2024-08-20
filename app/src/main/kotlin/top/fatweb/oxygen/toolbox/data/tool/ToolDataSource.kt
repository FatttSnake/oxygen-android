package top.fatweb.oxygen.toolbox.data.tool

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import top.fatweb.oxygen.toolbox.network.Dispatcher
import top.fatweb.oxygen.toolbox.network.OxygenDispatchers
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
}
