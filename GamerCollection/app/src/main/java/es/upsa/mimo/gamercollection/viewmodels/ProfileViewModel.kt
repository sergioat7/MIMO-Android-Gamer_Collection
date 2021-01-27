package es.upsa.mimo.gamercollection.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import es.upsa.mimo.gamercollection.models.AuthData
import es.upsa.mimo.gamercollection.models.ErrorResponse
import es.upsa.mimo.gamercollection.models.UserData
import es.upsa.mimo.gamercollection.network.apiClient.*
import es.upsa.mimo.gamercollection.repositories.*
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    private val sharedPreferencesHandler: SharedPreferencesHandler,
    private val formatAPIClient: FormatAPIClient,
    private val genreAPIClient: GenreAPIClient,
    private val platformAPIClient: PlatformAPIClient,
    private val stateAPIClient: StateAPIClient,
    private val userAPIClient: UserAPIClient,
    private val formatRepository: FormatRepository,
    private val gameRepository: GameRepository,
    private val genreRepository: GenreRepository,
    private val platformRepository: PlatformRepository,
    private val sagaRepository: SagaRepository,
    private val stateRepository: StateRepository
): ViewModel() {

    //MARK: - Private properties

    private val _profileLoading = MutableLiveData<Boolean>()
    private val _profileError = MutableLiveData<ErrorResponse>()

    //MARK: - Public properties

    val userData: UserData = sharedPreferencesHandler.getUserData()
    val language: String = sharedPreferencesHandler.getLanguage()
    val sortingKey: String = sharedPreferencesHandler.getSortingKey()
    val swipeRefresh: Boolean = sharedPreferencesHandler.getSwipeRefresh()
    val profileLoading: LiveData<Boolean> = _profileLoading
    val profileError: LiveData<ErrorResponse> = _profileError

    //MARK: - Public methods

    fun logout() {

        _profileLoading.value = true
        userAPIClient.logout({

            sharedPreferencesHandler.removePassword()
            resetDatabase()
        }, {

            sharedPreferencesHandler.removePassword()
            resetDatabase()
        })
    }

    fun save (newPassword: String, newLanguage: String, newSortParam: String, newSwipeRefresh: Boolean) {

        val changePassword = newPassword != sharedPreferencesHandler.getUserData().password
        val changeLanguage = newLanguage != sharedPreferencesHandler.getLanguage()
        val changeSortParam = newSortParam != sharedPreferencesHandler.getSortingKey()
        val changeSwipeRefresh = newSwipeRefresh != sharedPreferencesHandler.getSwipeRefresh()

        if (changePassword) {
            _profileLoading.value = true
            userAPIClient.updatePassword(newPassword, {

                sharedPreferencesHandler.storePassword(newPassword)
                val userData = sharedPreferencesHandler.getUserData()
                userAPIClient.login(userData.username, userData.password, {

                    val authData = AuthData(it)
                    sharedPreferencesHandler.storeCredentials(authData)
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
            sharedPreferencesHandler.setSortingKey(newSortParam)
        }

        if (changeSwipeRefresh) {
            sharedPreferencesHandler.setSwipeRefresh(newSwipeRefresh)
        }

        if (changeLanguage) {

            sharedPreferencesHandler.setLanguage(newLanguage)
            if (!changePassword) {
                reloadData()
            }
        }
    }

    fun deleteUser() {

        _profileLoading.value = true
        userAPIClient.deleteUser({

            sharedPreferencesHandler.removeUserData()
            sharedPreferencesHandler.removeCredentials()
            resetDatabase()
        }, {
            _profileError.value = it
        })
    }

    //MARK: - Private methods

    private fun reloadData() {

        _profileLoading.value = true

        formatAPIClient.getFormats({ formats ->
            genreAPIClient.getGenres({ genres ->
                platformAPIClient.getPlatforms({ platforms ->
                    stateAPIClient.getStates({ states ->

                        formatRepository.manageFormats(formats)
                        genreRepository.manageGenres(genres)
                        platformRepository.managePlatforms(platforms)
                        stateRepository.manageStates(states)

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
}