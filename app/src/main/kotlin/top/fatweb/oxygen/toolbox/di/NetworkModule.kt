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
                HttpLoggingInterceptor()
                    .apply {
                        if (BuildConfig.DEBUG) {
                            setLevel(HttpLoggingInterceptor.Level.BODY)
                        }
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