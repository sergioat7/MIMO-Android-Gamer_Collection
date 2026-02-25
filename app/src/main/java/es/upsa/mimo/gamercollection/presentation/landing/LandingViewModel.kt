package es.upsa.mimo.gamercollection.presentation.landing

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import es.upsa.mimo.gamercollection.presentation.login.LoginActivity
import es.upsa.mimo.gamercollection.presentation.MainActivity
import es.upsa.mimo.gamercollection.domain.GameRepository
import es.upsa.mimo.gamercollection.data.local.SharedPreferencesHelper
import javax.inject.Inject

@HiltViewModel
class LandingViewModel @Inject constructor(
    private val gameRepository: GameRepository
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
}