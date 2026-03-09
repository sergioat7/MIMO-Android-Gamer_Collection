package es.upsa.mimo.gamercollection.presentation.gamedetail.gamesongs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.upsa.mimo.gamercollection.domain.GameRepository
import es.upsa.mimo.gamercollection.domain.SongRepository
import es.upsa.mimo.gamercollection.domain.model.ErrorModel
import es.upsa.mimo.gamercollection.domain.model.Game
import es.upsa.mimo.gamercollection.domain.model.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GameSongsViewModel constructor(
    private var game: Game?,
    private val gameRepository: GameRepository,
    private val songRepository: SongRepository,
) : ViewModel() {

    //region Private properties
    private val _gameSongsLoading = MutableStateFlow<Boolean?>(null)
    private val _gameSongsError = MutableStateFlow<ErrorModel?>(null)
    private val _songs = MutableStateFlow(game?.songs ?: ArrayList())
    //endregion

    //region Public properties
    val gameSongsLoading: StateFlow<Boolean?> = _gameSongsLoading
    val gameSongsError: StateFlow<ErrorModel?> = _gameSongsError
    val songs: StateFlow<List<Song>> = _songs
    //endregion

    //region Public methods
    fun createSong(song: Song) {
        game?.let { game ->

            _gameSongsLoading.value = true
            viewModelScope.launch {
                val newSong = songRepository.createSong(game.id, song)
                val updatedGame = game.copy(songs = game.songs + listOf(newSong))
                gameRepository.updateGameDatabase(updatedGame)
                setGame(updatedGame)
                _gameSongsLoading.value = false
            }
        }
    }

    fun deleteSong(songId: Int) {
        game?.let { game ->

            _gameSongsLoading.value = true
            viewModelScope.launch {
                songRepository.deleteSong(game.id, songId)
                val updatedGame = game.copy(songs = game.songs.filter { it.id != songId })
                gameRepository.updateGameDatabase(updatedGame)
                setGame(updatedGame)
                _gameSongsLoading.value = false
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