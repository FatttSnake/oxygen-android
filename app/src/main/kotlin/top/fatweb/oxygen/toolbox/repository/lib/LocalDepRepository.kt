package top.fatweb.oxygen.toolbox.repository.lib

import kotlinx.coroutines.flow.Flow
import top.fatweb.oxygen.toolbox.data.lib.DepDataSource
import top.fatweb.oxygen.toolbox.model.lib.Dependencies
import javax.inject.Inject

class LocalDepRepository @Inject constructor(
    depDataSource: DepDataSource
) : DepRepository {
    override val dependencies: Flow<Dependencies> =
        depDataSource.dependencies
}