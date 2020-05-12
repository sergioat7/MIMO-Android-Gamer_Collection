package es.upsa.mimo.gamercollection.network.apiClient

import android.content.res.Resources
import es.upsa.mimo.gamercollection.models.ErrorResponse
import es.upsa.mimo.gamercollection.models.GenreResponse
import es.upsa.mimo.gamercollection.network.apiService.GenreAPIService
import es.upsa.mimo.gamercollection.utils.Constants
import java.util.*
import kotlin.collections.HashMap

class GenreAPIClient(
    private val resources: Resources
) {

    private val api = APIClient.getRetrofit().create(GenreAPIService::class.java)

    fun getGenres(success: (List<GenreResponse>) -> Unit, failure: (ErrorResponse) -> Unit) {

        val headers: MutableMap<String, String> = HashMap()
        headers[Constants.acceptLanguageHeader] = Locale.getDefault().language
        val request = api.getGenres(headers)

        APIClient.sendServer<List<GenreResponse>, ErrorResponse>(resources, request, { genres ->
            success(genres)
        }, { errorResponse ->
            failure(errorResponse)
        })
    }
}