package top.fatweb.oxygen.toolbox.repository.lib.impl

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import top.fatweb.oxygen.toolbox.data.lib.DepDataSource
import top.fatweb.oxygen.toolbox.model.lib.Dependencies
import top.fatweb.oxygen.toolbox.repository.lib.DepRepository
import javax.inject.Inject

class LocalDepRepository @Inject constructor(
    private val depDataSource: DepDataSource
) : DepRepository {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun searchName(name: String): Flow<Dependencies> =
        depDataSource.dependencies.flatMapLatest { dependencies ->
            flowOf(dependencies.copy(
                libraries = dependencies.libraries.filter {
                    it.name?.lowercase()?.contains(Regex("^.*${name.lowercase()}.*$")) ?: false
                }
            ))
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getSearchNameCount(): Flow<Int> =
        depDataSource.dependencies.flatMapLatest { flowOf(it.libraries.size) }
}
