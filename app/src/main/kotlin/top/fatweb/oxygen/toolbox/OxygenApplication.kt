package top.fatweb.oxygen.toolbox

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import top.fatweb.oxygen.toolbox.repository.userdata.UserDataRepository
import top.fatweb.oxygen.toolbox.util.LogTree
import javax.inject.Inject

@HiltAndroidApp
class OxygenApplication : Application() {
    @Inject
    lateinit var userDataRepository: UserDataRepository

    override fun onCreate() {
        super.onCreate()

        Timber.plant(LogTree(this))
    }
}
