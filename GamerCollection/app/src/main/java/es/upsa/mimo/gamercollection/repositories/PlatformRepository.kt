package es.upsa.mimo.gamercollection.repositories

import es.upsa.mimo.gamercollection.models.PlatformResponse
import es.upsa.mimo.gamercollection.persistence.AppDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class PlatformRepository @Inject constructor(
    private val database: AppDatabase
) {

    fun getPlatforms(): List<PlatformResponse> {

        var platforms = mutableListOf<PlatformResponse>()
        runBlocking {
            val result = GlobalScope.async { database.platformDao().getPlatforms() }
            platforms = result.await().toMutableList()
            platforms.sortBy { it.name }
            val other = platforms.firstOrNull { it.id == "OTHER" }
            platforms.remove(other)
            other?.let {
                platforms.add(it)
            }
        }
        return platforms
    }

    fun insertPlatform(platform: PlatformResponse) {

        GlobalScope.launch {
            database.platformDao().insertPlatform(platform)
        }
    }

    private fun deletePlatform(platform: PlatformResponse) {

        GlobalScope.launch {
            database.platformDao().deletePlatform(platform)
        }
    }

    fun removeDisableContent(newPlatforms: List<PlatformResponse>) {

        val currentPlatforms = getPlatforms()
        val platforms = AppDatabase.getDisabledContent(currentPlatforms, newPlatforms) as List<*>
        for (platform in platforms) {
            deletePlatform(platform as PlatformResponse)
        }
    }
}