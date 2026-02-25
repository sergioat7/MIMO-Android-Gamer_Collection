package es.upsa.mimo.gamercollection.presentation.gamedetail.gamesongs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.upsa.mimo.gamercollection.domain.GameRepository
import es.upsa.mimo.gamercollection.domain.SongRepository
import es.upsa.mimo.gamercollection.domain.model.ErrorModel
import es.upsa.mimo.gamercollection.domain.model.Game
import es.upsa.mimo.gamercollection.domain.model.Song
import kotlinx.coroutines.launch

class GameSongsViewModel constructor(
    private var game: Game?,
    private val gameRepository: GameRepository,
    private val songRepository: SongRepository
) : ViewModel() {

    //region Private properties
    private val _gameSongsLoading = MutableLiveData<Boolean>()
    private val _gameSongsError = MutableLiveData<ErrorModel>()
    private val _songs = MutableLiveData(game?.songs ?: ArrayList())
    //endregion

    //region Public properties
    val gameSongsLoading: LiveData<Boolean> = _gameSongsLoading
    val gameSongsError: LiveData<ErrorModel> = _gameSongsError
    val songs: LiveData<List<Song>> = _songs
    //endregion

    //region Public methods
    fun createSong(song: Song) {

        game?.let { game ->

            _gameSongsLoading.value = true
            viewModelScope.launch {
                songRepository.createSong(game.id, song, { newSong ->
//                    gameRepository.updateGameSongs(game.id, {

                    val updatedGame = game.copy(songs = game.songs + listOf(newSong))
                    gameRepository.updateGameDatabase(updatedGame)
                    setGame(updatedGame)
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

                    val updatedGame = game.copy(songs = game.songs.filter { it.id != songId })
                    gameRepository.updateGameDatabase(updatedGame)
                    setGame(updatedGame)
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
    //endregion

    //region Private methods
    private fun setGame(game: Game?) {

        this.game = game
        _songs.value = game?.songs ?: ArrayList()
    }
    //endregion
}