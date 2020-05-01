package es.upsa.mimo.gamercollection.persistence.daos

import androidx.room.*
import es.upsa.mimo.gamercollection.models.PlatformResponse

@Dao
interface PlatformDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPlatform(platform: PlatformResponse)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updatePlatform(platform: PlatformResponse)

    @Delete
    fun deletePlatform(platform: PlatformResponse)

    @Query("SELECT * FROM Platform WHERE id == :platformId")
    fun getPlatform(platformId: String): PlatformResponse

    @Query("SELECT * FROM Platform")
    fun getPlatforms(): List<PlatformResponse>
}