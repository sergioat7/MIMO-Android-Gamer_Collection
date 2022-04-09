package es.upsa.mimo.gamercollection.viewmodelfactories

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import es.upsa.mimo.gamercollection.GamerCollectionApplication
import es.upsa.mimo.gamercollection.repositories.GameRepository
import es.upsa.mimo.gamercollection.viewmodels.GameDetailViewModel
import javax.inject.Inject

class GameDetailViewModelFactory(
    private val application: Application?,
    private val gameId: Int,
    private val isRawgGame: Boolean
) : ViewModelProvider.Factory {

    //region Public properties
    @Inject
    lateinit var gameRepository: GameRepository

    @Inject
    lateinit var gameDetailViewModel: GameDetailViewModel
    //endregion

    //region  Lifecycle methods
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameDetailViewModel::class.java)) {

            (application as GamerCollectionApplication).appComponent.inject(this)
            gameDetailViewModel.setGameId(gameId)
            gameDetailViewModel.setIsRawgGame(isRawgGame)
            gameDetailViewModel.getGame()
            return gameDetailViewModel as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
    //endregion
}