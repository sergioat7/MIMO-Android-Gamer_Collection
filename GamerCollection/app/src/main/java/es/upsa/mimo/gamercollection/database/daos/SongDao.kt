package es.upsa.mimo.gamercollection.database.daos

import androidx.room.*
import es.upsa.mimo.gamercollection.models.SongResponse

@Dao
interface SongDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSong(song: SongResponse)

    @Delete
    suspend fun deleteSong(song: SongResponse)

    @Query("SELECT * FROM Song WHERE id == :songId")
    suspend fun getSong(songId: Int): SongResponse

    @Query("SELECT * FROM Song")
    suspend fun getSongs(): List<SongResponse>
}