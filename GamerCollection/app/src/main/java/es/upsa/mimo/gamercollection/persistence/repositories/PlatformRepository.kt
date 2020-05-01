package es.upsa.mimo.gamercollection.persistence.repositories

import android.content.Context
import es.upsa.mimo.gamercollection.models.PlatformResponse
import es.upsa.mimo.gamercollection.persistence.AppDatabase

class PlatformRepository(context: Context) {

    private val database = AppDatabase.getAppDatabase(context)
    private val platformDao = database.platformDao()

    fun getPlatforms(): List<PlatformResponse> {
        return platformDao.getPlatforms()
    }

    fun insertPlatform(platform: PlatformResponse) {
        platformDao.insertPlatform(platform)
    }
}