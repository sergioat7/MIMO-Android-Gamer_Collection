package es.upsa.mimo.gamercollection.persistence.daos

import androidx.room.*
import es.upsa.mimo.gamercollection.models.GenreResponse

@Dao
interface GenreDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGenre(genre: GenreResponse)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateGenre(genre: GenreResponse)

    @Delete
    fun deleteGenre(genre: GenreResponse)

    @Query("SELECT * FROM Genre WHERE id == :genreId")
    fun getGenre(genreId: String): GenreResponse

    @Query("SELECT * FROM Genre")
    fun getGenres(): List<GenreResponse>
}