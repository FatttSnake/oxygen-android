package top.fatweb.oxygen.toolbox.network.retrofit

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import top.fatweb.oxygen.toolbox.BuildConfig
import top.fatweb.oxygen.toolbox.data.network.OxygenNetworkDataSource
import top.fatweb.oxygen.toolbox.model.Result
import top.fatweb.oxygen.toolbox.model.asResult
import top.fatweb.oxygen.toolbox.network.model.PageVo
import top.fatweb.oxygen.toolbox.network.model.ResponseResult
import top.fatweb.oxygen.toolbox.network.model.ToolBaseVo
import top.fatweb.oxygen.toolbox.network.model.ToolVo
import javax.inject.Inject

private interface RetrofitOxygenNetworkApi {
    @GET(value = "/tool/store")
    suspend fun getStore(
        @Query("currentPage") currentPage: Int,
        @Query("searchValue") searchValue: String,
        @Query("platform") platform: ToolBaseVo.Platform? = ToolBaseVo.Platform.Android
    ): ResponseResult<PageVo<ToolVo>>

    @GET(value = "/tool/detail/{username}/{toolId}/{ver}")
    suspend fun detail(
        @Path("username") username: String,
        @Path("toolId") toolId: String,
        @Path("ver") ver: String,
        @Query("platform") platform: ToolBaseVo.Platform? = ToolBaseVo.Platform.Android
    ): ResponseResult<ToolVo>
}

private const val API_BASE_URL = BuildConfig.API_URL

internal class RetrofitOxygenNetwork @Inject constructor(
    networkJson: Json,
    okhttpCallFactory: dagger.Lazy<Call.Factory>
) : OxygenNetworkDataSource {
    private val networkApi = Retrofit.Builder()
        .baseUrl(API_BASE_URL)
        .callFactory { okhttpCallFactory.get().newCall(it) }
        .addConverterFactory(
            networkJson.asConverterFactory("application/json".toMediaType())
        )
        .build()
        .create(RetrofitOxygenNetworkApi::class.java)

    override suspend fun getStore(
        searchValue: String,
        currentPage: Int
    ): ResponseResult<PageVo<ToolVo>> =
        networkApi.getStore(searchValue = searchValue, currentPage = currentPage)

    override fun detail(
        username: String,
        toolId: String,
        ver: String,
        platform: ToolBaseVo.Platform
    ): Flow<Result<ToolVo>> = flow {
        emit(
            networkApi.detail(
                username = username,
                toolId = toolId,
                ver = ver,
                platform = platform
            )
        )
    }.asResult()
}
