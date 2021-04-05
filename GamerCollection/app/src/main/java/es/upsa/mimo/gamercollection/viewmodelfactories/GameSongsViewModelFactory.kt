package es.upsa.mimo.gamercollection.viewmodelfactories

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import es.upsa.mimo.gamercollection.GamerCollectionApplication
import es.upsa.mimo.gamercollection.models.responses.GameResponse
import es.upsa.mimo.gamercollection.repositories.GameRepository
import es.upsa.mimo.gamercollection.viewmodels.GameSongsViewModel
import javax.inject.Inject

class GameSongsViewModelFactory(
    private val application: Application?,
    private val game: GameResponse?
) : ViewModelProvider.Factory {

    //region Public properties
    @Inject
    lateinit var gameRepository: GameRepository

    @Inject
    lateinit var gameSongsViewModel: GameSongsViewModel
    //endregion

    //region Lifecycle methods
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameSongsViewModel::class.java)) {

            (application as GamerCollectionApplication).appComponent.inject(this)
            gameSongsViewModel.setGame(game)
            return gameSongsViewModel as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
    //endregion
}