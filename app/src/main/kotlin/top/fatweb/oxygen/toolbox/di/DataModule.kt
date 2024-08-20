package top.fatweb.oxygen.toolbox.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import top.fatweb.oxygen.toolbox.monitor.ConnectivityManagerNetworkMonitor
import top.fatweb.oxygen.toolbox.monitor.NetworkMonitor
import top.fatweb.oxygen.toolbox.monitor.TimeZoneBroadcastMonitor
import top.fatweb.oxygen.toolbox.monitor.TimeZoneMonitor
import top.fatweb.oxygen.toolbox.repository.lib.DepRepository
import top.fatweb.oxygen.toolbox.repository.lib.impl.LocalDepRepository
import top.fatweb.oxygen.toolbox.repository.tool.StoreRepository
import top.fatweb.oxygen.toolbox.repository.tool.ToolRepository
import top.fatweb.oxygen.toolbox.repository.tool.impl.NetworkStoreRepository
import top.fatweb.oxygen.toolbox.repository.tool.impl.OfflineToolRepository
import top.fatweb.oxygen.toolbox.repository.userdata.UserDataRepository
import top.fatweb.oxygen.toolbox.repository.userdata.impl.LocalUserDataRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    internal abstract fun bindsNetworkMonitor(networkMonitor: ConnectivityManagerNetworkMonitor): NetworkMonitor

    @Binds
    internal abstract fun bindsTimeZoneMonitor(timeZoneMonitor: TimeZoneBroadcastMonitor): TimeZoneMonitor

    @Binds
    internal abstract fun bindsUserDataRepository(userDataRepository: LocalUserDataRepository): UserDataRepository

    @Binds
    internal abstract fun bindsDepRepository(depRepository: LocalDepRepository): DepRepository

    @Binds
    internal abstract fun bindsStoreRepository(storeRepository: NetworkStoreRepository): StoreRepository

    @Binds
    internal abstract fun bindsToolRepository(toolRepository: OfflineToolRepository): ToolRepository
}
