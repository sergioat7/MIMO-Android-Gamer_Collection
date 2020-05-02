package es.upsa.mimo.gamercollection.network.apiClient

import android.content.res.Resources
import es.upsa.mimo.gamercollection.models.ErrorResponse
import es.upsa.mimo.gamercollection.models.PlatformResponse
import es.upsa.mimo.gamercollection.network.apiService.PlatformAPIService
import es.upsa.mimo.gamercollection.utils.Constants
import java.util.*
import kotlin.collections.HashMap

class PlatformAPIClient {
    companion object {

        fun getPlatforms(resources: Resources, success: (List<PlatformResponse>) -> Unit, failure: (ErrorResponse) -> Unit) {

            val headers: MutableMap<String, String> = HashMap()
            headers[Constants.acceptLanguageHeader] = Locale.getDefault().language
            val api = APIClient.getRetrofit().create(PlatformAPIService::class.java)
            val request = api.getPlatforms(headers)

            APIClient.sendServer<List<PlatformResponse>, ErrorResponse>(resources, request, { platforms ->
                success(platforms)
            }, { errorResponse ->
                failure(errorResponse)
            })
        }
    }
}