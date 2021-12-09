package tv.fanart.api

import retrofit2.http.GET
import retrofit2.http.Query
import tv.fanart.api.model.ActivityResponse

interface FanartApi {
    @GET(get<ConfigRepo>().updateConfig?.apiUrl ?: UpdateConfig.DEFAULT_API_URL)
    suspend fun getChanges(@Query("timestamp") after: Long): ActivityResponse
}