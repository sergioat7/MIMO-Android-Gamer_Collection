package es.upsa.mimo.gamercollection.repositories

import es.upsa.mimo.gamercollection.models.responses.ErrorResponse
import es.upsa.mimo.gamercollection.models.responses.PlatformResponse
import es.upsa.mimo.gamercollection.network.apiClient.PlatformAPIClient
import es.upsa.mimo.gamercollection.persistence.AppDatabase
import es.upsa.mimo.gamercollection.utils.Constants
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class PlatformRepository @Inject constructor(
    private val database: AppDatabase,
    private val platformAPIClient: PlatformAPIClient
) {

    // MARK: - Public methods

    fun loadPlatforms(success: () -> Unit, failure: (ErrorResponse) -> Unit) {

        platformAPIClient.getPlatforms({ newPlatforms ->

            for (newPlatform in newPlatforms) {
                insertPlatformDatabase(newPlatform)
            }
            val currentPlatforms = getPlatformsDatabase()
            val platformsToRemove = AppDatabase.getDisabledContent(currentPlatforms, newPlatforms)
            for (platform in platformsToRemove) {
                deletePlatformDatabase(platform as PlatformResponse)
            }
            success()
        }, failure)
    }

    fun getPlatformsDatabase(): List<PlatformResponse> {

        var platforms = mutableListOf<PlatformResponse>()
        runBlocking {
            val result = GlobalScope.async { database.platformDao().getPlatforms() }
            platforms = result.await().toMutableList()
            platforms.sortBy { it.name }
            val other = platforms.firstOrNull { it.id == Constants.DEFAULT_PLATFORM }
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

    // MARK: - Private methods

    private fun insertPlatformDatabase(platform: PlatformResponse) {

        GlobalScope.launch {
            database.platformDao().insertPlatform(platform)
        }
    }

    private fun deletePlatformDatabase(platform: PlatformResponse) {

        GlobalScope.launch {
            database.platformDao().deletePlatform(platform)
        }
    }
}