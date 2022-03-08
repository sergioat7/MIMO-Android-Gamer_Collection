package es.upsa.mimo.gamercollection.repositories

import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.injection.modules.IoDispatcher
import es.upsa.mimo.gamercollection.injection.modules.MainDispatcher
import es.upsa.mimo.gamercollection.models.responses.ErrorResponse
import es.upsa.mimo.gamercollection.models.responses.GenreResponse
import es.upsa.mimo.gamercollection.network.ApiManager
import es.upsa.mimo.gamercollection.network.GenreApiService
import es.upsa.mimo.gamercollection.network.RequestResult
import es.upsa.mimo.gamercollection.persistence.AppDatabase
import es.upsa.mimo.gamercollection.utils.Constants
import kotlinx.coroutines.*
import javax.inject.Inject

class GenreRepository @Inject constructor(
    private val api: GenreApiService,
    private val database: AppDatabase,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    //region Private properties
    private val externalScope = CoroutineScope(Job() + mainDispatcher)
    private val databaseScope = CoroutineScope(Job() + ioDispatcher)
    //endregion

    //region Public methods
    fun loadGenres(success: () -> Unit, failure: (ErrorResponse) -> Unit) {
        externalScope.launch {

            try {
                when (val response = ApiManager.validateResponse(api.getGenres())) {
                    is RequestResult.JsonSuccess -> {

                        val newGenres = response.body
                        for (newGenre in newGenres) {
                            insertGenreDatabase(newGenre)
                        }
                        val currentGenres = getGenresDatabase()
                        val genresToRemove =
                            AppDatabase.getDisabledContent(currentGenres, newGenres)
                        for (genre in genresToRemove) {
                            deleteGenreDatabase(genre as GenreResponse)
                        }
                        success()
                    }
                    is RequestResult.Failure -> failure(response.error)
                    else -> failure(ErrorResponse(Constants.EMPTY_VALUE, R.string.error_server))
                }
            } catch (e: Exception) {
                failure(ErrorResponse(Constants.EMPTY_VALUE, R.string.error_server_connection))
            }
        }
    }

    fun getGenresDatabase(): List<GenreResponse> {

        var genres = mutableListOf<GenreResponse>()
        runBlocking {

            val result = databaseScope.async {
                database.genreDao().getGenres()
            }
            genres = result.await().toMutableList()
            genres.sortBy { it.name }
            val other = genres.firstOrNull { it.id == ApiManager.OTHER_VALUE }
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
    //endregion

    //region Private methods
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
    //endregion
}