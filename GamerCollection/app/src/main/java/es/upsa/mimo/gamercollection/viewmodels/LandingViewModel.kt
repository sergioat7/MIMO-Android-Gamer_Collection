package es.upsa.mimo.gamercollection.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import es.upsa.mimo.gamercollection.activities.LoginActivity
import es.upsa.mimo.gamercollection.activities.MainActivity
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler
import javax.inject.Inject

class LandingViewModel @Inject constructor(
    private val sharedPreferencesHandler: SharedPreferencesHandler
): ViewModel() {

    //MARK: - Private properties

    private val _landingClassToStart = MutableLiveData<Class<*>>()

    //MARK: - Public properties

    val language: String
        get() = sharedPreferencesHandler.getLanguage()
    val landingClassToStart = _landingClassToStart

    //MARK: - Public methods

    fun checkVersion() {

        //TODO: check version

        _landingClassToStart.value = if (sharedPreferencesHandler.isLoggedIn()) {
            MainActivity::class.java
        } else {
            LoginActivity::class.java
        }
    }
}