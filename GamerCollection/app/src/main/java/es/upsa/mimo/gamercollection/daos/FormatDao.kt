package es.upsa.mimo.gamercollection.daos

import androidx.room.*
import es.upsa.mimo.gamercollection.models.FormatResponse

@Dao
interface FormatDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFormat(format: FormatResponse)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateFormat(format: FormatResponse)

    @Delete
    suspend fun deleteFormat(format: FormatResponse)

    @Query("SELECT * FROM Format WHERE id == :formatId")
    suspend fun getFormat(formatId: String): FormatResponse

    @Query("SELECT * FROM Format")
    suspend fun getFormats(): List<FormatResponse>
}