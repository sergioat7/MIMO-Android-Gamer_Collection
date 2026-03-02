package es.upsa.mimo.gamercollection.data.local.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import es.upsa.mimo.gamercollection.data.local.model.SongEntity

@Dao
interface SongDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSong(song: SongEntity)

    @Delete
    suspend fun deleteSong(song: SongEntity)

    @Query("SELECT * FROM Song WHERE id == :songId")
    suspend fun getSong(songId: Int): SongEntity?

    @Query("SELECT * FROM Song")
    suspend fun getSongs(): List<SongEntity>
}