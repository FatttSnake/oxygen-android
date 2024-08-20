package top.fatweb.oxygen.toolbox.data.lib

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.json.Json
import top.fatweb.oxygen.toolbox.R
import top.fatweb.oxygen.toolbox.model.lib.Dependencies
import top.fatweb.oxygen.toolbox.network.Dispatcher
import top.fatweb.oxygen.toolbox.network.OxygenDispatchers
import javax.inject.Inject

class DepDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    @Dispatcher(OxygenDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) {
    private val json = Json { ignoreUnknownKeys = true }

    val dependencies = flow {
        val inputStream = context.resources.openRawResource(R.raw.dependencies)
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        val dependencies = json.decodeFromString<Dependencies>(jsonString)
        emit(dependencies)
    }.flowOn(ioDispatcher)
}
