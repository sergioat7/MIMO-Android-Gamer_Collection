package es.upsa.mimo.gamercollection.persistence.repositories

import android.content.Context
import es.upsa.mimo.gamercollection.models.GenreResponse
import es.upsa.mimo.gamercollection.persistence.AppDatabase

class GenreRepository(context: Context) {

    private val database = AppDatabase.getAppDatabase(context)
    private val genreDao = database.genreDao()

    fun getGenres(): List<GenreResponse> {
        return genreDao.getGenres()
    }

    fun insertGenre(genre: GenreResponse) {
        genreDao.insertGenre(genre)
    }

    fun deleteGenre(genre: GenreResponse) {
        genreDao.deleteGenre(genre)
    }

    fun removeDisableContent(newGenres: List<GenreResponse>) {

        val currentGenres = getGenres()
        val genres = AppDatabase.getDisabledContent(currentGenres, newGenres)
        for (genre in genres) {
            deleteGenre(genre)
        }
    }
}