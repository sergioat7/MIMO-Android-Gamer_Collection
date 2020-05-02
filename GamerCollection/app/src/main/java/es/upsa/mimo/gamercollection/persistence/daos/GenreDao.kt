package es.upsa.mimo.gamercollection.persistence.daos

import androidx.room.*
import es.upsa.mimo.gamercollection.models.GenreResponse

@Dao
interface GenreDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGenre(genre: GenreResponse)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateGenre(genre: GenreResponse)

    @Delete
    suspend fun deleteGenre(genre: GenreResponse)

    @Query("SELECT * FROM Genre WHERE id == :genreId")
    suspend fun getGenre(genreId: String): GenreResponse

    @Query("SELECT * FROM Genre")
    suspend fun getGenres(): List<GenreResponse>
}