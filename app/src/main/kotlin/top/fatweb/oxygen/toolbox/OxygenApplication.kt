package top.fatweb.oxygen.toolbox

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import top.fatweb.oxygen.toolbox.repository.UserDataRepository
import javax.inject.Inject

@HiltAndroidApp
class OxygenApplication : Application() {
    @Inject
    lateinit var userDataRepository: UserDataRepository
}