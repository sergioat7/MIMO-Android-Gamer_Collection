package es.upsa.mimo.gamercollection.repositories

import es.upsa.mimo.gamercollection.models.GenreResponse
import es.upsa.mimo.gamercollection.persistence.AppDatabase
import es.upsa.mimo.gamercollection.utils.Constants
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class GenreRepository @Inject constructor(
    private val database: AppDatabase
) {

    // MARK: - Public methods

    fun getGenres(): List<GenreResponse> {

        var genres = mutableListOf<GenreResponse>()
        runBlocking {
            val result = GlobalScope.async { database.genreDao().getGenres() }
            genres = result.await().toMutableList()
            genres.sortBy { it.name }
            val other = genres.firstOrNull { it.id == Constants.DEFAULT_PLATFORM }
            genres.remove(other)
            other?.let {
                genres.add(it)
            }
        }
        return genres
    }

    private fun deleteGenre(genre: GenreResponse) {

        GlobalScope.launch {
            database.genreDao().deleteGenre(genre)
        }
    }

    fun manageGenres(newGenres: List<GenreResponse>) {

        for (newGenre in newGenres) {
            insertGenre(newGenre)
        }

        val currentGenres = getGenres()
        val genresToRemove = AppDatabase.getDisabledContent(currentGenres, newGenres)
        for (genre in genresToRemove) {
            deleteGenre(genre as GenreResponse)
        }
    }

    fun resetTable() {

        val genres = getGenres()
        for (genre in genres) {
            deleteGenre(genre)
        }
    }

    // MARK: - Private methods

    private fun insertGenre(genre: GenreResponse) {

        GlobalScope.launch {
            database.genreDao().insertGenre(genre)
        }
    }
}