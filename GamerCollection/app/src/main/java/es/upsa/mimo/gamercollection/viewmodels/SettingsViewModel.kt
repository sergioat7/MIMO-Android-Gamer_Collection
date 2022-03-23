package es.upsa.mimo.gamercollection.viewmodels

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import es.upsa.mimo.gamercollection.models.login.AuthData
import es.upsa.mimo.gamercollection.models.login.UserData
import es.upsa.mimo.gamercollection.models.responses.ErrorResponse
import es.upsa.mimo.gamercollection.repositories.*
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHelper
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val formatRepository: FormatRepository,
    private val gameRepository: GameRepository,
    private val genreRepository: GenreRepository,
    private val platformRepository: PlatformRepository,
    private val sagaRepository: SagaRepository,
    private val stateRepository: StateRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    //region Private properties
    private val _settingsLoading = MutableLiveData<Boolean>()
    private val _settingsError = MutableLiveData<ErrorResponse?>()
    //endregion

    //region Public properties
    val userData: UserData
        get() = SharedPreferencesHelper.userData
    val language: String = SharedPreferencesHelper.language
    var sortParam: String = SharedPreferencesHelper.sortParam
    var swipeRefresh: Boolean = SharedPreferencesHelper.swipeRefresh
    val settingsLoading: LiveData<Boolean> = _settingsLoading
    val settingsError: LiveData<ErrorResponse?> = _settingsError
    //endregion

    //region Public methods
    fun logout() {

        _settingsLoading.value = true
        userRepository.logout()
        SharedPreferencesHelper.removePassword()
        resetDatabase()
    }

    fun save(
        newPassword: String,
        newLanguage: String,
        newSortParam: String,
        newSwipeRefresh: Boolean,
        themeMode: Int
    ) {

        val changePassword =            newPassword != userData.password
        val changeLanguage =            newLanguage != language
        val changeSortParam =           newSortParam != sortParam
        val changeSwipeRefresh =        newSwipeRefresh != swipeRefresh
        val changeThemeMode =           themeMode != SharedPreferencesHelper.themeMode

        if (changePassword) {
            _settingsLoading.value = true
            userRepository.updatePassword(newPassword, {

                SharedPreferencesHelper.storePassword(newPassword)
                val userData = SharedPreferencesHelper.userData
                userRepository.login(userData.username, userData.password, {

                    SharedPreferencesHelper.credentials = AuthData(it)
                    _settingsLoading.value = false
                    if (changeLanguage || changeSortParam) {
                        reloadData()
                    }
                }, {
                    _settingsError.value = it
                })
            }, {
                _settingsError.value = it
            })
        }

        if (changeLanguage) {
            SharedPreferencesHelper.language = newLanguage
        }

        if (changeSortParam) {

            SharedPreferencesHelper.sortParam = newSortParam
            sortParam = newSortParam
        }

        if (changeSwipeRefresh) {

            SharedPreferencesHelper.swipeRefresh = newSwipeRefresh
            swipeRefresh = newSwipeRefresh
        }

        if (changeThemeMode) {

            SharedPreferencesHelper.themeMode = themeMode
            when (themeMode) {
                1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }

        if (!changePassword && (changeLanguage || changeSortParam)) {
            reloadData()
        }
    }

    fun deleteUser() {

        _settingsLoading.value = true
        userRepository.deleteUser({

            SharedPreferencesHelper.removeUserData()
            SharedPreferencesHelper.removeCredentials()
            resetDatabase()
        }, {
            _settingsError.value = it
        })
    }
    //endregion

    //region Private methods
    private fun reloadData() {

        _settingsLoading.value = true

        formatRepository.loadFormats({
            genreRepository.loadGenres({
                platformRepository.loadPlatforms({
                    stateRepository.loadStates({

                        _settingsLoading.value = false
                        _settingsError.value = null
                    }, {
                        _settingsError.value = it
                    })
                }, {
                    _settingsError.value = it
                })
            }, {
                _settingsError.value = it
            })
        }, {
            _settingsError.value = it
        })
    }

    private fun resetDatabase() {

        gameRepository.resetTable()
        sagaRepository.resetTable()

        _settingsLoading.value = false
        _settingsError.value = null
    }
    //endregion
}