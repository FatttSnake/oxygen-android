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
import top.fatweb.oxygen.toolbox.repository.tool.impl.NetworkToolRepository
import top.fatweb.oxygen.toolbox.repository.tool.ToolRepository
import top.fatweb.oxygen.toolbox.repository.userdata.impl.LocalUserDataRepository
import top.fatweb.oxygen.toolbox.repository.userdata.UserDataRepository

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
    internal abstract fun bindsToolRepository(toolRepository: NetworkToolRepository): ToolRepository

    @Binds
    internal abstract fun bindsDepRepository(depRepository: LocalDepRepository): DepRepository
}