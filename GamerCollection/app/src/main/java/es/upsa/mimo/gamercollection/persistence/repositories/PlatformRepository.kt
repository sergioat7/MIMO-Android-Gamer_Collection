package es.upsa.mimo.gamercollection.persistence.repositories

import android.content.Context
import es.upsa.mimo.gamercollection.models.GenreResponse
import es.upsa.mimo.gamercollection.models.PlatformResponse
import es.upsa.mimo.gamercollection.persistence.AppDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class PlatformRepository(context: Context) {

    private val database = AppDatabase.getAppDatabase(context)
    private val platformDao = database.platformDao()

    fun getPlatforms(): List<PlatformResponse> {

        var platforms = mutableListOf<PlatformResponse>()
        runBlocking {
            val result = GlobalScope.async { platformDao.getPlatforms() }
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
            platformDao.insertPlatform(platform)
        }
    }

    private fun deletePlatform(platform: PlatformResponse) {

        GlobalScope.launch {
            platformDao.deletePlatform(platform)
        }
    }

    fun removeDisableContent(newPlatforms: List<PlatformResponse>) {

        val currentPlatforms = getPlatforms()
        val platforms = AppDatabase.getDisabledContent(currentPlatforms, newPlatforms) as List<PlatformResponse>
        for (platform in platforms) {
            deletePlatform(platform)
        }
    }
}