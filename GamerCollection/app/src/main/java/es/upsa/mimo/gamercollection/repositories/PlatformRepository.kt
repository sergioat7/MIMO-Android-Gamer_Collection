package es.upsa.mimo.gamercollection.repositories

import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.injection.modules.IoDispatcher
import es.upsa.mimo.gamercollection.injection.modules.MainDispatcher
import es.upsa.mimo.gamercollection.models.responses.ErrorResponse
import es.upsa.mimo.gamercollection.models.responses.PlatformResponse
import es.upsa.mimo.gamercollection.network.ApiManager
import es.upsa.mimo.gamercollection.network.PlatformApiService
import es.upsa.mimo.gamercollection.network.RequestResult
import es.upsa.mimo.gamercollection.persistence.AppDatabase
import es.upsa.mimo.gamercollection.utils.Constants
import kotlinx.coroutines.*
import javax.inject.Inject

class PlatformRepository @Inject constructor(
    private val api: PlatformApiService,
    private val database: AppDatabase,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    //region Private properties
    private val externalScope = CoroutineScope(Job() + mainDispatcher)
    private val databaseScope = CoroutineScope(Job() + ioDispatcher)
    //endregion

    //region Public methods
    fun loadPlatforms(success: () -> Unit, failure: (ErrorResponse) -> Unit) {
        externalScope.launch {

            try {
                when (val response = ApiManager.validateResponse(api.getPlatforms())) {
                    is RequestResult.JsonSuccess -> {

                        val newPlatforms = response.body
                        for (newPlatform in newPlatforms) {
                            insertPlatformDatabase(newPlatform)
                        }
                        val currentPlatforms = getPlatformsDatabase()
                        val platformsToRemove =
                            AppDatabase.getDisabledContent(currentPlatforms, newPlatforms)
                        for (platform in platformsToRemove) {
                            deletePlatformDatabase(platform as PlatformResponse)
                        }
                        success()
                    }
                    is RequestResult.Failure -> failure(response.error)
                    else -> failure(ErrorResponse(Constants.EMPTY_VALUE, R.string.error_server))
                }
            } catch (e: Exception) {
                failure(ErrorResponse(Constants.EMPTY_VALUE, R.string.error_server_connection))
            }
        }
    }

    fun getPlatformsDatabase(): List<PlatformResponse> {

        var platforms = mutableListOf<PlatformResponse>()
        runBlocking {

            val result = databaseScope.async {
                database.platformDao().getPlatforms()
            }
            platforms = result.await().toMutableList()
            platforms.sortBy { it.name }
            val other = platforms.firstOrNull { it.id == ApiManager.OTHER_VALUE }
            platforms.remove(other)
            other?.let {
                platforms.add(it)
            }
        }
        return platforms
    }

    fun resetTable() {

        val platforms = getPlatformsDatabase()
        for (platform in platforms) {
            deletePlatformDatabase(platform)
        }
    }
    //endregion

    //region Private methods
    private fun insertPlatformDatabase(platform: PlatformResponse) {

        runBlocking {
            val job = databaseScope.launch {
                database.platformDao().insertPlatform(platform)
            }
            job.join()
        }
    }

    private fun deletePlatformDatabase(platform: PlatformResponse) {

        runBlocking {
            val job = databaseScope.launch {
                database.platformDao().deletePlatform(platform)
            }
            job.join()
        }
    }
    //endregion
}