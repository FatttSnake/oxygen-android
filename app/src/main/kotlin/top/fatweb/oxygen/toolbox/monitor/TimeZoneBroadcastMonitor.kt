package top.fatweb.oxygen.toolbox.monitor

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Build.VERSION_CODES
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.shareIn
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toKotlinTimeZone
import top.fatweb.oxygen.toolbox.di.ApplicationScope
import top.fatweb.oxygen.toolbox.network.Dispatcher
import top.fatweb.oxygen.toolbox.network.OxygenDispatchers
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.seconds

@Singleton
class TimeZoneBroadcastMonitor @Inject constructor(
    @ApplicationContext private val context: Context,
    @ApplicationScope appScope: CoroutineScope,
    @Dispatcher(OxygenDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : TimeZoneMonitor {
    override val currentTimeZone: SharedFlow<TimeZone> = callbackFlow {
        trySend(TimeZone.currentSystemDefault())

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action != Intent.ACTION_TIMEZONE_CHANGED) return

                val zonIdFromIntent = if (Build.VERSION.SDK_INT < VERSION_CODES.R) {
                    null
                } else {
                    intent.getStringExtra(Intent.EXTRA_TIMEZONE)?.let { timeZoneId ->
                        val zoneId = ZoneId.of(timeZoneId, ZoneId.SHORT_IDS)
                        zoneId.toKotlinTimeZone()
                    }
                }

                trySend(zonIdFromIntent ?: TimeZone.currentSystemDefault())
            }
        }

        context.registerReceiver(receiver, IntentFilter(Intent.ACTION_TIMEZONE_CHANGED))

        trySend(TimeZone.currentSystemDefault())

        awaitClose {
            context.unregisterReceiver(receiver)
        }
    }
        .distinctUntilChanged()
        .conflate()
        .flowOn(ioDispatcher)
        .shareIn(appScope, SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds), 1)
}