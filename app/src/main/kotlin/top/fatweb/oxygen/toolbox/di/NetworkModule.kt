package top.fatweb.oxygen.toolbox.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import top.fatweb.oxygen.toolbox.BuildConfig
import top.fatweb.oxygen.toolbox.data.network.OxygenNetworkDataSource
import top.fatweb.oxygen.toolbox.network.retrofit.RetrofitOxygenNetwork
import top.fatweb.oxygen.toolbox.util.HttpLogger
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {
    @Provides
    @Singleton
    fun providesNetworkJson(): Json = Json {
        ignoreUnknownKeys = true
    }

    @Provides
    @Singleton
    fun okHttpCallFactory(): Call.Factory =
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor(HttpLogger())
                    .apply {
                        level =
                            if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.BASIC
                    }
            )
            .build()

    @Provides
    @Singleton
    fun providesOxygenNetworkDataSource(
        networkJson: Json,
        okhttpCallFactory: dagger.Lazy<Call.Factory>
    ): OxygenNetworkDataSource = RetrofitOxygenNetwork(networkJson, okhttpCallFactory)
}