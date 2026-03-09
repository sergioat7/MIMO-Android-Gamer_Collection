package es.upsa.mimo.gamercollection.presentation.landing

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import es.upsa.mimo.gamercollection.domain.GameRepository
import es.upsa.mimo.gamercollection.domain.UserRepository
import es.upsa.mimo.gamercollection.presentation.MainActivity
import es.upsa.mimo.gamercollection.presentation.login.LoginActivity
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class LandingViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    //region Private properties
    private val _landingClassToStart = MutableStateFlow<Class<*>?>(null)
    //endregion

    //region Public properties
    val language: String
        get() = userRepository.language
    val newChangesPopupShown: Boolean
        get() = userRepository.newChangesPopupShown
    val landingClassToStart: StateFlow<Class<*>?> = _landingClassToStart
    //endregion

    //region Public methods
    fun checkTheme() {
        when (userRepository.themeMode) {
            1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM,
            )
        }
    }

    fun checkIsLoggedIn() {
        userRepository.storeNewChangesPopupShown(true)
        _landingClassToStart.value = if (userRepository.isLoggedIn) {
            MainActivity::class.java
        } else {
            LoginActivity::class.java
        }
    }

    fun fetchRemoteConfigValues() {
        gameRepository.fetchRemoteConfigValues(language)
    }
    //endregion
}