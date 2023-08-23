package es.upsa.mimo.gamercollection.data.source

import es.upsa.mimo.gamercollection.data.source.di.IoDispatcher
import es.upsa.mimo.gamercollection.models.ErrorResponse
import es.upsa.mimo.gamercollection.models.SongResponse
import es.upsa.mimo.gamercollection.network.interfaces.SongApiService
import es.upsa.mimo.gamercollection.database.AppDatabase
import kotlinx.coroutines.*
import javax.inject.Inject

class SongRepository @Inject constructor(
    private val api: SongApiService,
    private val database: AppDatabase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    //region Private properties
    private val databaseScope = CoroutineScope(Job() + ioDispatcher)
    //endregion

    //region Public methods
    suspend fun createSong(
        gameId: Int,
        newSong: SongResponse,
        success: () -> Unit,
        failure: (ErrorResponse) -> Unit
    ) {
        newSong.id = getNextId()
        insertSongDatabase(newSong)
        success()
//        try {
//            when (val response = ApiManager.validateResponse(api.createSong(gameId, newSong))) {
//
//                is RequestResult.Success -> success()
//                is RequestResult.Failure -> failure(response.error)
//                else -> failure(ErrorResponse(Constants.EMPTY_VALUE, R.string.error_server))
//            }
//        } catch (e: Exception) {
//            failure(ErrorResponse(Constants.EMPTY_VALUE, R.string.error_server_connection))
//        }
    }

    suspend fun deleteSong(
        gameId: Int,
        songId: Int,
        success: () -> Unit,
        failure: (ErrorResponse) -> Unit
    ) {
        getSongDatabase(songId)?.let {
            deleteSongDatabase(it)
        }
        success()
//        try {
//            when (val response = ApiManager.validateResponse(api.deleteSong(gameId, songId))) {
//
//                is RequestResult.Success -> success()
//                is RequestResult.Failure -> failure(response.error)
//                else -> failure(ErrorResponse(Constants.EMPTY_VALUE, R.string.error_server))
//            }
//        } catch (e: Exception) {
//            failure(ErrorResponse(Constants.EMPTY_VALUE, R.string.error_server_connection))
//        }
    }
    //endregion

    //region Private methods
    private fun getSongsDatabase(): List<SongResponse> {

        var songs: List<SongResponse> = arrayListOf()
        runBlocking {

            val result = databaseScope.async {
                database.songDao().getSongs()
            }
            songs = result.await()
        }
        return songs
    }

    private fun getSongDatabase(songId: Int): SongResponse? {

        var song: SongResponse? = null
        runBlocking {

            val result = databaseScope.async {
                database.songDao().getSong(songId)
            }
            song = result.await()
        }
        return song
    }

    private fun insertSongDatabase(song: SongResponse) {

        runBlocking {
            val job = databaseScope.launch {
                database.songDao().insertSong(song)
            }
            job.join()
        }
    }

    private fun deleteSongDatabase(song: SongResponse) {

        runBlocking {
            val job = databaseScope.launch {
                database.songDao().deleteSong(song)
            }
            job.join()
        }
    }

    private fun getNextId(): Int {

        val songs = getSongsDatabase()
        return if (songs.isNotEmpty()) {
            songs.maxOf { it.id } + 1
        } else {
            0
        }
    }
    //endregion
}