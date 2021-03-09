package es.upsa.mimo.gamercollection.daos

import androidx.room.*
import es.upsa.mimo.gamercollection.models.responses.PlatformResponse

@Dao
interface PlatformDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlatform(platform: PlatformResponse)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updatePlatform(platform: PlatformResponse)

    @Delete
    suspend fun deletePlatform(platform: PlatformResponse)

    @Query("SELECT * FROM Platform WHERE id == :platformId")
    suspend fun getPlatform(platformId: String): PlatformResponse

    @Query("SELECT * FROM Platform")
    suspend fun getPlatforms(): List<PlatformResponse>
}