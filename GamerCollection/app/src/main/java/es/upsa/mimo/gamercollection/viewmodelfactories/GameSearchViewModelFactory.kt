package es.upsa.mimo.gamercollection.viewmodelfactories

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import es.upsa.mimo.gamercollection.injection.GamerCollectionApplication
import es.upsa.mimo.gamercollection.network.apiClient.GameAPIClient
import es.upsa.mimo.gamercollection.network.apiClient.PlatformAPIClient
import es.upsa.mimo.gamercollection.network.apiClient.StateAPIClient
import es.upsa.mimo.gamercollection.repositories.GameRepository
import es.upsa.mimo.gamercollection.repositories.PlatformRepository
import es.upsa.mimo.gamercollection.repositories.StateRepository
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler
import es.upsa.mimo.gamercollection.viewmodels.GameSearchViewModel
import javax.inject.Inject

class GameSearchViewModelFactory(
    private val application: Application?
): ViewModelProvider.Factory {

    //MARK: - Public properties

    @Inject
    lateinit var sharedPrefHandler: SharedPreferencesHandler
    @Inject
    lateinit var gameAPIClient: GameAPIClient
    @Inject
    lateinit var platformAPIClient: PlatformAPIClient
    @Inject
    lateinit var stateAPIClient: StateAPIClient
    @Inject
    lateinit var gameRepository: GameRepository
    @Inject
    lateinit var platformRepository: PlatformRepository
    @Inject
    lateinit var stateRepository: StateRepository
    @Inject
    lateinit var gameSearchViewModel: GameSearchViewModel

    //MARK: - Lifecycle methods

    @Suppress("UNCHECKED_CAST")
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameSearchViewModel::class.java)) {

            (application as GamerCollectionApplication).appComponent.inject(this)
            gameSearchViewModel.loadGames()
            return gameSearchViewModel as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}