package es.upsa.mimo.gamercollection.viewmodelfactories

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import es.upsa.mimo.gamercollection.injection.GamerCollectionApplication
import es.upsa.mimo.gamercollection.network.apiClient.*
import es.upsa.mimo.gamercollection.repositories.*
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler
import es.upsa.mimo.gamercollection.viewmodels.LoginViewModel
import javax.inject.Inject

class LoginViewModelFactory(
    private val application: Application?
) : ViewModelProvider.Factory {

    //region Public properties
    @Inject
    lateinit var sharedPrefHandler: SharedPreferencesHandler

    @Inject
    lateinit var formatAPIClient: FormatAPIClient

    @Inject
    lateinit var gameAPIClient: GameAPIClient

    @Inject
    lateinit var genreAPIClient: GenreAPIClient

    @Inject
    lateinit var platformAPIClient: PlatformAPIClient

    @Inject
    lateinit var sagaAPIClient: SagaAPIClient

    @Inject
    lateinit var stateAPIClient: StateAPIClient

    @Inject
    lateinit var userAPIClient: UserAPIClient

    @Inject
    lateinit var formatRepository: FormatRepository

    @Inject
    lateinit var gameRepository: GameRepository

    @Inject
    lateinit var genreRepository: GenreRepository

    @Inject
    lateinit var platformRepository: PlatformRepository

    @Inject
    lateinit var sagaRepository: SagaRepository

    @Inject
    lateinit var stateRepository: StateRepository

    @Inject
    lateinit var loginViewModel: LoginViewModel
    //endregion

    //region Lifecycle methods
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {

            (application as GamerCollectionApplication).appComponent.inject(this)
            return loginViewModel as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
    //endregion
}