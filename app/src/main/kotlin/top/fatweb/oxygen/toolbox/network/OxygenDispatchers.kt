package top.fatweb.oxygen.toolbox.network

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Dispatcher(val oxygenDispatcher: OxygenDispatchers)

enum class OxygenDispatchers {
    Default,
    IO
}