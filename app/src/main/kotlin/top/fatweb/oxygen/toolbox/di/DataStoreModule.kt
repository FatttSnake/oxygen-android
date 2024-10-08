package top.fatweb.oxygen.toolbox.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import top.fatweb.oxygen.toolbox.datastore.IntToStringIdsMigration
import top.fatweb.oxygen.toolbox.datastore.UserPreferences
import top.fatweb.oxygen.toolbox.datastore.UserPreferencesSerializer
import top.fatweb.oxygen.toolbox.network.Dispatcher
import top.fatweb.oxygen.toolbox.network.OxygenDispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    @Provides
    @Singleton
    internal fun providesUserPreferencesDataStore(
        @ApplicationContext context: Context,
        @Dispatcher(OxygenDispatchers.IO) ioDispatcher: CoroutineDispatcher,
        @ApplicationScope scope: CoroutineScope,
        userPreferencesSerializer: UserPreferencesSerializer
    ): DataStore<UserPreferences> =
        DataStoreFactory.create(
            serializer = userPreferencesSerializer,
            scope = CoroutineScope(scope.coroutineContext + ioDispatcher),
            migrations = listOf(
                IntToStringIdsMigration
            )
        ) {
            context.dataStoreFile("user_preferences.pb")
        }
}