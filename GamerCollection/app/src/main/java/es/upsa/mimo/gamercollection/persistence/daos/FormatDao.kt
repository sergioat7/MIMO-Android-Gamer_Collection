package es.upsa.mimo.gamercollection.persistence.daos

import androidx.room.*
import es.upsa.mimo.gamercollection.models.FormatResponse

@Dao
interface FormatDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFormat(format: FormatResponse)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateFormat(format: FormatResponse)

    @Delete
    fun deleteFormat(format: FormatResponse)

    @Query("SELECT * FROM Format WHERE id == :formatId")
    fun getFormat(formatId: String): FormatResponse

    @Query("SELECT * FROM Format")
    fun getFormats(): List<FormatResponse>
}