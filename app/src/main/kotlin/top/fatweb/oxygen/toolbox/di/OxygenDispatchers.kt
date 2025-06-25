package top.fatweb.oxygen.toolbox.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Dispatcher(val oxygenDispatcher: OxygenDispatchers)

enum class OxygenDispatchers {
    Default,
    IO
}
