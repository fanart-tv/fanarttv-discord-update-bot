package tv.fanart.api

import retrofit2.http.GET
import retrofit2.http.Query
import tv.fanart.api.model.ActivityResponse
import tv.fanart.config.ConfigRepo
import tv.fanart.config.model.UpdateConfig

interface FanartApi {
    @GET(get<ConfigRepo>().updateConfig?.apiUrl ?: UpdateConfig.DEFAULT_API_URL)
    suspend fun getChanges(@Query("timestamp") after: Long): ActivityResponse
}