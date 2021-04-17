package es.upsa.mimo.gamercollection.viewmodelfactories

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import es.upsa.mimo.gamercollection.GamerCollectionApplication
import es.upsa.mimo.gamercollection.models.responses.GameResponse
import es.upsa.mimo.gamercollection.repositories.FormatRepository
import es.upsa.mimo.gamercollection.repositories.GenreRepository
import es.upsa.mimo.gamercollection.viewmodels.GameDataViewModel
import javax.inject.Inject

class GameDataViewModelFactory(
    private val application: Application?,
    private val game: GameResponse?
) : ViewModelProvider.Factory {

    //region Public properties
    @Inject
    lateinit var formatRepository: FormatRepository

    @Inject
    lateinit var genreRepository: GenreRepository

    @Inject
    lateinit var gameDataViewModel: GameDataViewModel
    //endregion

    //region Lifecycle methods
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameDataViewModel::class.java)) {

            (application as GamerCollectionApplication).appComponent.inject(this)
            gameDataViewModel.setGame(game)
            return gameDataViewModel as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
    //endregion
}