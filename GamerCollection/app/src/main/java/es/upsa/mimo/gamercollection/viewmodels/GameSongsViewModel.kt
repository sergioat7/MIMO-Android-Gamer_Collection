package es.upsa.mimo.gamercollection.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import es.upsa.mimo.gamercollection.models.responses.ErrorResponse
import es.upsa.mimo.gamercollection.models.responses.GameResponse
import es.upsa.mimo.gamercollection.models.responses.SongResponse
import es.upsa.mimo.gamercollection.network.apiClient.SongAPIClient
import es.upsa.mimo.gamercollection.repositories.GameRepository
import javax.inject.Inject

class GameSongsViewModel @Inject constructor(
    private val songAPIClient: SongAPIClient,
    private val gameRepository: GameRepository
): ViewModel() {

    //MARK: - Private properties

    private var game: GameResponse? = null
    private val _gameSongsLoading = MutableLiveData<Boolean>()
    private val _gameSongsError = MutableLiveData<ErrorResponse>()
    private val _songs = MutableLiveData<List<SongResponse>>()

    //MARK: - Public properties

    val gameSongsLoading: LiveData<Boolean> = _gameSongsLoading
    val gameSongsError: LiveData<ErrorResponse> = _gameSongsError
    val songs: LiveData<List<SongResponse>> = _songs

    //MARK: - Public methods

    fun createSong(song: SongResponse) {

        game?.let { game ->

            _gameSongsLoading.value = true
            songAPIClient.createSong(game.id, song, {
                gameRepository.updateGameSongs(game.id, {

                    this.game = it
                    _songs.value = it.songs
                    _gameSongsLoading.value = false
                }, {
                    _gameSongsError.value = it
                })
            }, {
                _gameSongsError.value = it
            })
        }
    }

    fun deleteSong(songId: Int) {

        game?.let { game ->

            _gameSongsLoading.value = true
            songAPIClient.deleteSong(game.id, songId, {
                gameRepository.updateGameSongs(game.id, {

                    this.game = it
                    _songs.value = it.songs
                    _gameSongsLoading.value = false
                }, {
                    _gameSongsError.value = it
                })
            }, {
                _gameSongsError.value = it
            })
        }
    }

    fun setGame(game: GameResponse?) {

        this.game = game
        _songs.value = game?.songs ?: ArrayList()
    }
}