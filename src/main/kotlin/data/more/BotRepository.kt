package data.more

import com.google.gson.Gson
import data.model.LatestModel
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Value

class BotRepository(
    private val valueApi: ValueApi,
) {

    @Value("\${telegram.valueKey}")
    private val valueKey: String = ""
    suspend fun getCurrentValue(symbol: String, period: String, access_key: String): LatestModel {
        println("botrepository $symbol $period $access_key")

        return withContext(Dispatchers.IO) {
            valueApi.getLatestId(symbol = symbol, period = period, access_key = access_key)
        }.await()
    }

    suspend fun readId(symbol: String): LatestModel {
        val client = HttpClient(CIO) {

            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.HEADERS
                filter { request ->
                    request.url.host.contains("ktor.io")
                }
            }
            followRedirects = false
            expectSuccess = false
        }
        val data = client.get("https://fcsapi.com/api-v3/forex/latest?&symbol=${symbol}&period=1h&access_key=${valueKey}").body<String>()

        return Gson().fromJson(data, LatestModel::class.java)
    }
}