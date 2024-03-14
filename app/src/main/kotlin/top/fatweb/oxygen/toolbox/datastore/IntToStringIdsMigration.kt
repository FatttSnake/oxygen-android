package top.fatweb.oxygen.toolbox.datastore

import androidx.datastore.core.DataMigration

internal object IntToStringIdsMigration : DataMigration<UserPreferences> {
    override suspend fun cleanUp() = Unit

    override suspend fun migrate(currentData: UserPreferences): UserPreferences =
        currentData.copy { }

    override suspend fun shouldMigrate(currentData: UserPreferences): Boolean = false
}