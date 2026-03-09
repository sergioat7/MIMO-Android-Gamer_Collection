package es.upsa.mimo.gamercollection.presentation.gamedetail.gamedata

import androidx.lifecycle.ViewModel
import es.upsa.mimo.gamercollection.domain.UserRepository
import es.upsa.mimo.gamercollection.domain.model.ErrorModel
import es.upsa.mimo.gamercollection.domain.model.Game
import java.util.Date
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class GameDataViewModel(
    private val game: Game?,
    private val userRepository: UserRepository,
) : ViewModel() {

    //region Private properties
    private val _gameDataLoading = MutableStateFlow<Boolean?>(null)
    private val _gameDataError = MutableStateFlow<ErrorModel?>(null)
    //endregion

    //region Public properties
    val language: String
        get() = userRepository.language
    val dateFormatToShow: String
        get() = userRepository.dateFormatToShow
    val gameDataLoading: StateFlow<Boolean?> = _gameDataLoading
    val gameDataError: StateFlow<ErrorModel?> = _gameDataError
    //endregion

    //region Public methods
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
        observations: String,
    ): Game? {
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
            return Game(
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
                ArrayList(),
            )
        }
    }
    //endregion
}