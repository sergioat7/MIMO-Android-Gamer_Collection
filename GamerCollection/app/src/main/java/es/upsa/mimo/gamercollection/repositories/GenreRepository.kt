package es.upsa.mimo.gamercollection.repositories

import es.upsa.mimo.gamercollection.models.responses.ErrorResponse
import es.upsa.mimo.gamercollection.models.responses.GenreResponse
import es.upsa.mimo.gamercollection.network.apiClient.GenreAPIClient
import es.upsa.mimo.gamercollection.persistence.AppDatabase
import es.upsa.mimo.gamercollection.utils.Constants
import kotlinx.coroutines.*
import javax.inject.Inject

class GenreRepository @Inject constructor(
    private val database: AppDatabase,
    private val genreAPIClient: GenreAPIClient
) {

    // MARK: - Private properties

    private val databaseScope = CoroutineScope(Job() + Dispatchers.IO)

    // MARK: - Public methods

    fun loadGenres(success: () -> Unit, failure: (ErrorResponse) -> Unit) {

        genreAPIClient.getGenres({ newGenres ->

            for (newGenre in newGenres) {
                insertGenreDatabase(newGenre)
            }
            val currentGenres = getGenresDatabase()
            val genresToRemove = AppDatabase.getDisabledContent(currentGenres, newGenres)
            for (genre in genresToRemove) {
                deleteGenreDatabase(genre as GenreResponse)
            }
            success()
        }, failure)
    }

    fun getGenresDatabase(): List<GenreResponse> {

        var genres = mutableListOf<GenreResponse>()
        runBlocking {

            val result = databaseScope.async {
                database.genreDao().getGenres()
            }
            genres = result.await().toMutableList()
            genres.sortBy { it.name }
            val other = genres.firstOrNull { it.id == Constants.OTHER_VALUE }
            genres.remove(other)
            other?.let {
                genres.add(it)
            }
        }
        return genres
    }

    fun resetTable() {

        val genres = getGenresDatabase()
        for (genre in genres) {
            deleteGenreDatabase(genre)
        }
    }

    // MARK: - Private methods

    private fun insertGenreDatabase(genre: GenreResponse) {

        runBlocking {
            val job = databaseScope.launch {
                database.genreDao().insertGenre(genre)
            }
            job.join()
        }
    }

    private fun deleteGenreDatabase(genre: GenreResponse) {

        runBlocking {
            val job = databaseScope.launch {
                database.genreDao().deleteGenre(genre)
            }
            job.join()
        }
    }
}