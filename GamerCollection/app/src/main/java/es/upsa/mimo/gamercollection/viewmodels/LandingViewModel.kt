package es.upsa.mimo.gamercollection.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import es.upsa.mimo.gamercollection.BuildConfig
import es.upsa.mimo.gamercollection.activities.LoginActivity
import es.upsa.mimo.gamercollection.activities.MainActivity
import es.upsa.mimo.gamercollection.repositories.*
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler
import javax.inject.Inject

class LandingViewModel @Inject constructor(
    private val sharedPreferencesHandler: SharedPreferencesHandler,
    private val formatRepository: FormatRepository,
    private val gameRepository: GameRepository,
    private val genreRepository: GenreRepository,
    private val platformRepository: PlatformRepository,
    private val sagaRepository: SagaRepository,
    private val stateRepository: StateRepository
): ViewModel() {

    //MARK: - Private properties

    private val _landingClassToStart = MutableLiveData<Class<*>>()

    //MARK: - Public properties

    val language: String
        get() = sharedPreferencesHandler.getLanguage()
    val landingClassToStart = _landingClassToStart

    //MARK: - Public methods

    fun checkVersion() {

        val currentVersion = sharedPreferencesHandler.getVersion()
        val newVersion = BuildConfig.VERSION_CODE
        if (newVersion > currentVersion) {

            sharedPreferencesHandler.setVersion(newVersion)
            sharedPreferencesHandler.removePassword()
            sharedPreferencesHandler.removeCredentials()
            resetDatabase()
            _landingClassToStart.value = LoginActivity::class.java
        } else {

            _landingClassToStart.value = if (sharedPreferencesHandler.isLoggedIn()) {
                MainActivity::class.java
            } else {
                LoginActivity::class.java
            }
        }
    }

    //MARK: - Private methods

    private fun resetDatabase() {

        formatRepository.resetTable()
        gameRepository.resetTable()
        genreRepository.resetTable()
        platformRepository.resetTable()
        sagaRepository.resetTable()
        stateRepository.resetTable()
    }
}