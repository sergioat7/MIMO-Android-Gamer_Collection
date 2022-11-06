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
    private val gameRepository: GameRepository,
    private val sagaRepository: SagaRepository
) : ViewModel() {

    //region Private properties
    private val _landingClassToStart = MutableLiveData<Class<*>>()
    //endregion

    //region Public properties
    val language: String
        get() = SharedPreferencesHelper.language
    val newChangesPopupShown: Boolean
        get() = SharedPreferencesHelper.newChangesPopupShown
    val landingClassToStart = _landingClassToStart
    //endregion

    //region Public methods
    fun checkTheme() {

        when (SharedPreferencesHelper.themeMode) {
            1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    fun checkIsLoggedIn() {

        SharedPreferencesHelper.newChangesPopupShown = true
        _landingClassToStart.value = if (SharedPreferencesHelper.isLoggedIn) {
            MainActivity::class.java
        } else {
            LoginActivity::class.java
        }
    }
    //endregion

    //region Private methods
    private fun resetDatabase() {

        gameRepository.resetTable()
        sagaRepository.resetTable()
    }
    //endregion
}