package es.upsa.mimo.gamercollection.network.apiClient

import es.upsa.mimo.gamercollection.models.ErrorResponse
import es.upsa.mimo.gamercollection.models.SongResponse
import es.upsa.mimo.gamercollection.network.apiService.SongAPIService
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler
import javax.inject.Inject

class SongAPIClient @Inject constructor(
    private val sharedPrefHandler: SharedPreferencesHandler
) {

    // MARK: - Private properties

    private val api = APIClient.retrofit.create(SongAPIService::class.java)

    // MARK: - Public methods

    fun createSong(gameId: Int, song: SongResponse, success: () -> Unit, failure: (ErrorResponse) -> Unit) {

        val headers: MutableMap<String, String> = HashMap()
        headers[Constants.ACCEPT_LANGUAGE_HEADER] = sharedPrefHandler.getLanguage()
        headers[Constants.AUTHORIZATION_HEADER] = sharedPrefHandler.getCredentials().token
        val request = api.createSong(headers, gameId, song)

        APIClient.sendServer<Void, ErrorResponse>(request, {
            success()
        }, {
            failure(it)
        })
    }

    fun deleteSong(gameId: Int, songId: Int, success: () -> Unit, failure: (ErrorResponse) -> Unit) {

        val headers: MutableMap<String, String> = HashMap()
        headers[Constants.ACCEPT_LANGUAGE_HEADER] = sharedPrefHandler.getLanguage()
        headers[Constants.AUTHORIZATION_HEADER] = sharedPrefHandler.getCredentials().token
        val request = api.deleteSong(headers, gameId, songId)

        APIClient.sendServer<Void, ErrorResponse>(request, {
            success()
        }, {
            failure(it)
        })
    }
}