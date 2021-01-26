package es.upsa.mimo.gamercollection.viewmodelfactories

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import es.upsa.mimo.gamercollection.injection.GamerCollectionApplication
import es.upsa.mimo.gamercollection.models.GameResponse
import es.upsa.mimo.gamercollection.network.apiClient.GameAPIClient
import es.upsa.mimo.gamercollection.repositories.FormatRepository
import es.upsa.mimo.gamercollection.repositories.GameRepository
import es.upsa.mimo.gamercollection.repositories.GenreRepository
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler
import es.upsa.mimo.gamercollection.viewmodels.GameDataViewModel
import javax.inject.Inject

class GameDataViewModelFactory(
    private val application: Application?,
    private val game: GameResponse?
): ViewModelProvider.Factory {

    //MARK: - Public properties

    @Inject
    lateinit var sharedPrefHandler: SharedPreferencesHandler
    @Inject
    lateinit var gameAPIClient: GameAPIClient
    @Inject
    lateinit var formatRepository: FormatRepository
    @Inject
    lateinit var gameRepository: GameRepository
    @Inject
    lateinit var genreRepository: GenreRepository
    @Inject
    lateinit var gameDataViewModel: GameDataViewModel

    //MARK: - Lifecycle methods

    @Suppress("UNCHECKED_CAST")
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameDataViewModel::class.java)) {

            (application as GamerCollectionApplication).appComponent.inject(this)
            gameDataViewModel.setGame(game)
            return gameDataViewModel as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}