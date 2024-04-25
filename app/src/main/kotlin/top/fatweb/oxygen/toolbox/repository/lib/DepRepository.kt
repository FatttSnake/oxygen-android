package top.fatweb.oxygen.toolbox.repository.lib

import kotlinx.coroutines.flow.Flow
import top.fatweb.oxygen.toolbox.model.lib.Dependencies

interface DepRepository {
    fun searchName(name: String): Flow<Dependencies>

    fun getSearchNameCount(): Flow<Int>
}