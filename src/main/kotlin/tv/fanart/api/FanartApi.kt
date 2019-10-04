package tv.fanart.api

import retrofit2.http.GET
import retrofit2.http.Query
import tv.fanart.api.model.ActivityResponse

interface FanartApi {
    @GET("v3.2/activity")
    suspend fun getChanges(@Query("timestamp") after: Long): ActivityResponse
}