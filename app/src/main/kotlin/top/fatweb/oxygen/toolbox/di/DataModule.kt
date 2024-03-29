package top.fatweb.oxygen.toolbox.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import top.fatweb.oxygen.toolbox.monitor.ConnectivityManagerNetworkMonitor
import top.fatweb.oxygen.toolbox.monitor.NetworkMonitor
import top.fatweb.oxygen.toolbox.monitor.TimeZoneBroadcastMonitor
import top.fatweb.oxygen.toolbox.monitor.TimeZoneMonitor
import top.fatweb.oxygen.toolbox.repository.tool.OfflineFirstToolRepository
import top.fatweb.oxygen.toolbox.repository.tool.ToolRepository
import top.fatweb.oxygen.toolbox.repository.userdata.OfflineFirstUserDataRepository
import top.fatweb.oxygen.toolbox.repository.userdata.UserDataRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    internal abstract fun bindsNetworkMonitor(networkMonitor: ConnectivityManagerNetworkMonitor): NetworkMonitor

    @Binds
    internal abstract fun bindsTimeZoneMonitor(timeZoneMonitor: TimeZoneBroadcastMonitor): TimeZoneMonitor

    @Binds
    internal abstract fun bindsUserDataRepository(userDataRepository: OfflineFirstUserDataRepository): UserDataRepository

    @Binds
    internal abstract fun bindsToolRepository(toolRepository: OfflineFirstToolRepository): ToolRepository
}