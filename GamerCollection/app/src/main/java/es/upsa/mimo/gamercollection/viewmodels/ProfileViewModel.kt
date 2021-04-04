package es.upsa.mimo.gamercollection.viewmodels

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import es.upsa.mimo.gamercollection.models.login.AuthData
import es.upsa.mimo.gamercollection.models.login.UserData
import es.upsa.mimo.gamercollection.models.responses.ErrorResponse
import es.upsa.mimo.gamercollection.network.apiClient.UserAPIClient
import es.upsa.mimo.gamercollection.repositories.*
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    private val formatRepository: FormatRepository,
    private val gameRepository: GameRepository,
    private val genreRepository: GenreRepository,
    private val platformRepository: PlatformRepository,
    private val sagaRepository: SagaRepository,
    private val stateRepository: StateRepository
) : ViewModel() {

    //region Private properties
    private val userAPIClient = UserAPIClient()
    private val _profileLoading = MutableLiveData<Boolean>()
    private val _profileError = MutableLiveData<ErrorResponse>()
    //endregion

    //region Public properties
    val userData: UserData
        get() = SharedPreferencesHandler.getUserData()
    val language: String
        get() = SharedPreferencesHandler.getLanguage()
    val sortingKey: String
        get() = SharedPreferencesHandler.getSortingKey()
    val swipeRefresh: Boolean
        get() = SharedPreferencesHandler.getSwipeRefresh()
    val profileLoading: LiveData<Boolean> = _profileLoading
    val profileError: LiveData<ErrorResponse> = _profileError
    //endregion

    //region Public methods
    fun logout() {

        _profileLoading.value = true
        userAPIClient.logout()
        SharedPreferencesHandler.removePassword()
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
        val changeThemeMode = themeMode != SharedPreferencesHandler.getThemeMode()

        if (changePassword) {
            _profileLoading.value = true
            userAPIClient.updatePassword(newPassword, {

                SharedPreferencesHandler.storePassword(newPassword)
                val userData = SharedPreferencesHandler.getUserData()
                userAPIClient.login(userData.username, userData.password, {

                    val authData = AuthData(it)
                    SharedPreferencesHandler.storeCredentials(authData)
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
            SharedPreferencesHandler.setSortingKey(newSortParam)
        }

        if (changeSwipeRefresh) {
            SharedPreferencesHandler.setSwipeRefresh(newSwipeRefresh)
        }

        if (changeLanguage) {

            SharedPreferencesHandler.setLanguage(newLanguage)
            if (!changePassword) {
                reloadData()
            }
        }

        if (changeThemeMode) {

            SharedPreferencesHandler.setThemeMode(themeMode)
            when (themeMode) {
                1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }

    fun deleteUser() {

        _profileLoading.value = true
        userAPIClient.deleteUser({

            SharedPreferencesHandler.removeUserData()
            SharedPreferencesHandler.removeCredentials()
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