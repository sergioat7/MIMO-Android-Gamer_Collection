package es.upsa.mimo.gamercollection.viewmodelfactories

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import es.upsa.mimo.gamercollection.injection.GamerCollectionApplication
import es.upsa.mimo.gamercollection.network.apiClient.*
import es.upsa.mimo.gamercollection.repositories.FormatRepository
import es.upsa.mimo.gamercollection.repositories.GenreRepository
import es.upsa.mimo.gamercollection.repositories.PlatformRepository
import es.upsa.mimo.gamercollection.repositories.StateRepository
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler
import es.upsa.mimo.gamercollection.viewmodels.RegisterViewModel
import javax.inject.Inject

class RegisterViewModelFactory(
    private val application: Application?
) : ViewModelProvider.Factory {

    //MARK: - Public properties

    @Inject
    lateinit var sharedPrefHandler: SharedPreferencesHandler

    @Inject
    lateinit var formatAPIClient: FormatAPIClient

    @Inject
    lateinit var genreAPIClient: GenreAPIClient

    @Inject
    lateinit var platformAPIClient: PlatformAPIClient

    @Inject
    lateinit var stateAPIClient: StateAPIClient

    @Inject
    lateinit var userAPIClient: UserAPIClient

    @Inject
    lateinit var formatRepository: FormatRepository

    @Inject
    lateinit var genreRepository: GenreRepository

    @Inject
    lateinit var platformRepository: PlatformRepository

    @Inject
    lateinit var stateRepository: StateRepository

    @Inject
    lateinit var registerViewModel: RegisterViewModel

    //MARK: - Lifecycle methods

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {

            (application as GamerCollectionApplication).appComponent.inject(this)
            return registerViewModel as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}