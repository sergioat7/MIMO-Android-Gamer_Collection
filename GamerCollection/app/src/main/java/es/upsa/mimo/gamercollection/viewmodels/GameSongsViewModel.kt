package es.upsa.mimo.gamercollection.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.upsa.mimo.gamercollection.models.responses.ErrorResponse
import es.upsa.mimo.gamercollection.models.responses.GameResponse
import es.upsa.mimo.gamercollection.models.responses.SongResponse
import es.upsa.mimo.gamercollection.repositories.GameRepository
import es.upsa.mimo.gamercollection.repositories.SongRepository
import kotlinx.coroutines.launch

class GameSongsViewModel constructor(
    private var game: GameResponse?,
    private val gameRepository: GameRepository,
    private val songRepository: SongRepository
) : ViewModel() {

    //region Private properties
    private val _gameSongsLoading = MutableLiveData<Boolean>()
    private val _gameSongsError = MutableLiveData<ErrorResponse>()
    private val _songs = MutableLiveData<List<SongResponse>>()
    //endregion

    //region Public properties
    val gameSongsLoading: LiveData<Boolean> = _gameSongsLoading
    val gameSongsError: LiveData<ErrorResponse> = _gameSongsError
    val songs: LiveData<List<SongResponse>> = _songs
    //endregion

    //region Public methods
    fun createSong(song: SongResponse) {

        game?.let { game ->

            _gameSongsLoading.value = true
            viewModelScope.launch {
                songRepository.createSong(game.id, song, {
//                    gameRepository.updateGameSongs(game.id, {

                    game.songs.add(song)
                    gameRepository.updateGameDatabase(game)
                    setGame(game)
                    _gameSongsLoading.value = false
//                    }, {
//                        _gameSongsError.value = it
//                    })
                }, {
                    _gameSongsError.value = it
                })
            }
        }
    }

    fun deleteSong(songId: Int) {

        game?.let { game ->

            _gameSongsLoading.value = true
            viewModelScope.launch {
                songRepository.deleteSong(game.id, songId, {
//                    gameRepository.updateGameSongs(game.id, {

                    game.songs.removeAll { it.id == songId }
                    gameRepository.updateGameDatabase(game)
                    setGame(game)
                    _gameSongsLoading.value = false
//                    }, {
//                        _gameSongsError.value = it
//                    })
                }, {
                    _gameSongsError.value = it
                })
            }
        }
    }

    fun setGame(game: GameResponse?) {

        this.game = game
        _songs.value = game?.songs ?: ArrayList()
    }
    //endregion
}