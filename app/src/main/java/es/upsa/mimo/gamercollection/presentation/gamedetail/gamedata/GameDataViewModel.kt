package es.upsa.mimo.gamercollection.presentation.gamedetail.gamedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import es.upsa.mimo.gamercollection.data.local.SharedPreferencesHelper
import es.upsa.mimo.gamercollection.domain.model.ErrorModel
import es.upsa.mimo.gamercollection.domain.model.Game
import java.util.*

class GameDataViewModel(private val game: Game?) : ViewModel() {

    //region Private properties
    private val _gameDataLoading = MutableLiveData<Boolean>()
    private val _gameDataError = MutableLiveData<ErrorModel>()
    //endregion

    //region Public properties
    val language: String
        get() = SharedPreferencesHelper.language
    val dateFormatToShow: String
        get() = SharedPreferencesHelper.dateFormatToShow
    val gameDataLoading: LiveData<Boolean> = _gameDataLoading
    val gameDataError: LiveData<ErrorModel> = _gameDataError
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
        observations: String
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
                ArrayList()
            )
        }
    }
    //endregion
}