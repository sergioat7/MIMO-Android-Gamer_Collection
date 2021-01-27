package es.upsa.mimo.gamercollection.repositories

import es.upsa.mimo.gamercollection.models.PlatformResponse
import es.upsa.mimo.gamercollection.persistence.AppDatabase
import es.upsa.mimo.gamercollection.utils.Constants
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class PlatformRepository @Inject constructor(
    private val database: AppDatabase
) {

    // MARK: - Public methods

    fun getPlatforms(): List<PlatformResponse> {

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

    private fun deletePlatform(platform: PlatformResponse) {

        GlobalScope.launch {
            database.platformDao().deletePlatform(platform)
        }
    }

    fun managePlatforms(newPlatforms: List<PlatformResponse>) {

        for (newPlatform in newPlatforms) {
            insertPlatform(newPlatform)
        }

        val currentPlatforms = getPlatforms()
        val platformsToRemove = AppDatabase.getDisabledContent(currentPlatforms, newPlatforms)
        for (platform in platformsToRemove) {
            deletePlatform(platform as PlatformResponse)
        }
    }

    fun resetTable() {

        val platforms = getPlatforms()
        for (platform in platforms) {
            deletePlatform(platform)
        }
    }

    // MARK: - Private methods

    private fun insertPlatform(platform: PlatformResponse) {

        GlobalScope.launch {
            database.platformDao().insertPlatform(platform)
        }
    }
}