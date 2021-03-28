package es.upsa.mimo.gamercollection.viewmodelfactories

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import es.upsa.mimo.gamercollection.injection.GamerCollectionApplication
import es.upsa.mimo.gamercollection.network.apiClient.SagaAPIClient
import es.upsa.mimo.gamercollection.repositories.GameRepository
import es.upsa.mimo.gamercollection.repositories.PlatformRepository
import es.upsa.mimo.gamercollection.repositories.SagaRepository
import es.upsa.mimo.gamercollection.repositories.StateRepository
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler
import es.upsa.mimo.gamercollection.viewmodels.SagaDetailViewModel
import javax.inject.Inject

class SagaDetailViewModelFactory(
    private val application: Application?,
    private val sagaId: Int?
) : ViewModelProvider.Factory {

    //MARK: - Public properties

    @Inject
    lateinit var sharedPrefHandler: SharedPreferencesHandler

    @Inject
    lateinit var sagaAPIClient: SagaAPIClient

    @Inject
    lateinit var gameRepository: GameRepository

    @Inject
    lateinit var platformRepository: PlatformRepository

    @Inject
    lateinit var sagaRepository: SagaRepository

    @Inject
    lateinit var stateRepository: StateRepository

    @Inject
    lateinit var sagaDetailViewModel: SagaDetailViewModel

    //MARK: - Lifecycle methods

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SagaDetailViewModel::class.java)) {

            (application as GamerCollectionApplication).appComponent.inject(this)
            sagaDetailViewModel.setSagaId(sagaId)
            sagaDetailViewModel.getSaga()
            return sagaDetailViewModel as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}