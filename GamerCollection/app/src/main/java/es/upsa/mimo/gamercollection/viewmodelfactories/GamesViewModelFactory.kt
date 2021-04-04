package es.upsa.mimo.gamercollection.viewmodelfactories

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import es.upsa.mimo.gamercollection.injection.GamerCollectionApplication
import es.upsa.mimo.gamercollection.network.apiClient.GameAPIClient
import es.upsa.mimo.gamercollection.network.apiClient.PlatformAPIClient
import es.upsa.mimo.gamercollection.repositories.GameRepository
import es.upsa.mimo.gamercollection.repositories.PlatformRepository
import es.upsa.mimo.gamercollection.viewmodels.GamesViewModel
import javax.inject.Inject

class GamesViewModelFactory(
    private val application: Application?
) : ViewModelProvider.Factory {

    //region Public properties
    @Inject
    lateinit var gameRepository: GameRepository

    @Inject
    lateinit var platformRepository: PlatformRepository

    @Inject
    lateinit var gamesViewModel: GamesViewModel
    //endregion

    //region Lifecycle methods
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GamesViewModel::class.java)) {

            (application as GamerCollectionApplication).appComponent.inject(this)
            return gamesViewModel as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
    //endregion
}