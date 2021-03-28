package es.upsa.mimo.gamercollection.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import es.upsa.mimo.gamercollection.models.responses.ErrorResponse
import es.upsa.mimo.gamercollection.models.responses.FormatResponse
import es.upsa.mimo.gamercollection.models.responses.GameResponse
import es.upsa.mimo.gamercollection.models.responses.GenreResponse
import es.upsa.mimo.gamercollection.repositories.FormatRepository
import es.upsa.mimo.gamercollection.repositories.GameRepository
import es.upsa.mimo.gamercollection.repositories.GenreRepository
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class GameDataViewModel @Inject constructor(
    private val sharedPreferencesHandler: SharedPreferencesHandler,
    private val formatRepository: FormatRepository,
    private val gameRepository: GameRepository,
    private val genreRepository: GenreRepository
) : ViewModel() {

    //MARK: - Private properties

    private var game: GameResponse? = null
    private val _gameDataLoading = MutableLiveData<Boolean>()
    private val _gameDataError = MutableLiveData<ErrorResponse>()

    //MARK: - Public properties

    val language: String
        get() = sharedPreferencesHandler.getLanguage()
    val formats: List<FormatResponse>
        get() = formatRepository.getFormatsDatabase()
    val genres: List<GenreResponse>
        get() = genreRepository.getGenresDatabase()
    val gameDataLoading: LiveData<Boolean> = _gameDataLoading
    val gameDataError: LiveData<ErrorResponse> = _gameDataError

    //MARK: - Public methods

    fun setGame(game: GameResponse?) {
        this.game = game
    }

    fun getGameData(
        pegi: String?,
        distributor: String,
        developer: String,
        players: String,
        releaseDate: Date?,
        goty: Boolean,
        format: String?,
        genre: String?,
        state: String?,
        purchaseDate: Date?,
        purchaseLocation: String,
        price: Double,
        videoUrl: String,
        loanedTo: String,
        observations: String
    ): GameResponse? {

        if (pegi == null &&
            distributor.isEmpty() &&
            developer.isEmpty() &&
            players.isEmpty() &&
            releaseDate == null &&
            !goty &&
            format == null &&
            genre == null &&
            state == null &&
            purchaseDate == null &&
            purchaseLocation.isEmpty() &&
            price == 0.0 &&
            videoUrl.isEmpty() &&
            loanedTo.isEmpty() &&
            observations.isEmpty()
        ) {
            return null
        } else {
            return GameResponse(
                game?.id ?: 0,
                null,
                null,
                0.0,
                pegi,
                distributor,
                developer,
                players,
                releaseDate,
                goty,
                format,
                genre,
                state,
                purchaseDate,
                purchaseLocation,
                price,
                null,
                videoUrl,
                loanedTo,
                observations,
                game?.saga,
                ArrayList()
            )
        }
    }
}