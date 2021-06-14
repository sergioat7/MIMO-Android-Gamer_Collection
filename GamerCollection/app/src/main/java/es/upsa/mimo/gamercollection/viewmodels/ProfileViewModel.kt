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

class ProfileViewModel @Inject constructor(
    private val formatRepository: FormatRepository,
    private val gameRepository: GameRepository,
    private val genreRepository: GenreRepository,
    private val platformRepository: PlatformRepository,
    private val sagaRepository: SagaRepository,
    private val stateRepository: StateRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    //region Private properties
    private val _profileLoading = MutableLiveData<Boolean>()
    private val _profileError = MutableLiveData<ErrorResponse>()
    //endregion

    //region Public properties
    val userData: UserData
        get() = SharedPreferencesHelper.getUserData()
    val language: String
        get() = SharedPreferencesHelper.getLanguage()
    val sortingKey: String
        get() = SharedPreferencesHelper.getSortingKey()
    val swipeRefresh: Boolean
        get() = SharedPreferencesHelper.getSwipeRefresh()
    val profileLoading: LiveData<Boolean> = _profileLoading
    val profileError: LiveData<ErrorResponse> = _profileError
    //endregion

    //region Public methods
    fun logout() {

        _profileLoading.value = true
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

        val changePassword = newPassword != userData.password
        val changeLanguage = newLanguage != language
        val changeSortParam = newSortParam != sortingKey
        val changeSwipeRefresh = newSwipeRefresh != swipeRefresh
        val changeThemeMode = themeMode != SharedPreferencesHelper.getThemeMode()

        if (changePassword) {
            _profileLoading.value = true
            userRepository.updatePassword(newPassword, {

                SharedPreferencesHelper.storePassword(newPassword)
                val userData = SharedPreferencesHelper.getUserData()
                userRepository.login(userData.username, userData.password, {

                    val authData = AuthData(it)
                    SharedPreferencesHelper.storeCredentials(authData)
                    _profileLoading.value = false
                    if (changeLanguage) {
                        reloadData()
                    }
                }, {
                    _profileError.value = it
                })
            }, {
                _profileError.value = it
            })
        }

        if (changeSortParam) {
            SharedPreferencesHelper.setSortingKey(newSortParam)
        }

        if (changeSwipeRefresh) {
            SharedPreferencesHelper.setSwipeRefresh(newSwipeRefresh)
        }

        if (changeLanguage) {

            SharedPreferencesHelper.setLanguage(newLanguage)
            if (!changePassword) {
                reloadData()
            }
        }

        if (changeThemeMode) {

            SharedPreferencesHelper.setThemeMode(themeMode)
            when (themeMode) {
                1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }

    fun deleteUser() {

        _profileLoading.value = true
        userRepository.deleteUser({

            SharedPreferencesHelper.removeUserData()
            SharedPreferencesHelper.removeCredentials()
            resetDatabase()
        }, {
            _profileError.value = it
        })
    }
    //endregion

    //region Private methods
    private fun reloadData() {

        _profileLoading.value = true

        formatRepository.loadFormats({
            genreRepository.loadGenres({
                platformRepository.loadPlatforms({
                    stateRepository.loadStates({

                        _profileLoading.value = false
                        _profileError.value = null
                    }, {
                        _profileError.value = it
                    })
                }, {
                    _profileError.value = it
                })
            }, {
                _profileError.value = it
            })
        }, {
            _profileError.value = it
        })
    }

    private fun resetDatabase() {

        gameRepository.resetTable()
        sagaRepository.resetTable()

        _profileLoading.value = false
        _profileError.value = null
    }
    //endregion
}