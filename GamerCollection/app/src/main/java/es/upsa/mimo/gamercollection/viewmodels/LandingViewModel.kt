package es.upsa.mimo.gamercollection.viewmodels

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import es.upsa.mimo.gamercollection.BuildConfig
import es.upsa.mimo.gamercollection.activities.LoginActivity
import es.upsa.mimo.gamercollection.activities.MainActivity
import es.upsa.mimo.gamercollection.repositories.*
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHelper
import javax.inject.Inject

class LandingViewModel @Inject constructor(
    private val formatRepository: FormatRepository,
    private val gameRepository: GameRepository,
    private val genreRepository: GenreRepository,
    private val platformRepository: PlatformRepository,
    private val sagaRepository: SagaRepository,
    private val stateRepository: StateRepository
) : ViewModel() {

    //region Private properties
    private val _landingClassToStart = MutableLiveData<Class<*>>()
    //endregion

    //region Public properties
    val language: String
        get() = SharedPreferencesHelper.language
    val landingClassToStart = _landingClassToStart
    //endregion

    //region Public methods
    fun checkVersion() {

        val currentVersion = SharedPreferencesHelper.version
        val newVersion = BuildConfig.VERSION_CODE
        if (newVersion > currentVersion) {

            SharedPreferencesHelper.version = newVersion
            SharedPreferencesHelper.removePassword()
            SharedPreferencesHelper.removeCredentials()
            resetDatabase()
            _landingClassToStart.value = LoginActivity::class.java
        } else {

            _landingClassToStart.value = if (SharedPreferencesHelper.isLoggedIn) {
                MainActivity::class.java
            } else {
                LoginActivity::class.java
            }
        }
    }

    fun checkTheme() {

        when (SharedPreferencesHelper.themeMode) {
            1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }
    //endregion

    //region Private methods
    private fun resetDatabase() {

        formatRepository.resetTable()
        gameRepository.resetTable()
        genreRepository.resetTable()
        platformRepository.resetTable()
        sagaRepository.resetTable()
        stateRepository.resetTable()
    }
    //endregion
}