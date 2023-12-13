package data.more

import data.model.LatestModel
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Query

interface ValueApi {
    @GET("latest?")
    fun getLatestId(
        @Query("symbol") symbol: String,
        @Query("period") period: String,
        @Query("access_key") access_key: String,
    ): Deferred<LatestModel>
}