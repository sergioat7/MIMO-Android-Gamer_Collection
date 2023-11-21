package es.upsa.mimo.gamercollection.ui.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.data.source.GameRepository
import es.upsa.mimo.gamercollection.data.source.SagaRepository
import es.upsa.mimo.gamercollection.data.source.SharedPreferencesHelper
import es.upsa.mimo.gamercollection.data.source.UserRepository
import es.upsa.mimo.gamercollection.models.AuthData
import es.upsa.mimo.gamercollection.models.ErrorResponse
import es.upsa.mimo.gamercollection.models.GameResponse
import es.upsa.mimo.gamercollection.models.SagaResponse
import es.upsa.mimo.gamercollection.models.UserData
import es.upsa.mimo.gamercollection.utils.Constants
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val sagaRepository: SagaRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    //region Private properties
    private val _settingsForm = MutableLiveData<Int?>()
    private val _settingsLoading = MutableLiveData<Boolean>()
    private val _settingsError = MutableLiveData<ErrorResponse?>()
    //endregion

    //region Public properties
    val userData: UserData
        get() = SharedPreferencesHelper.userData
    val language: String = SharedPreferencesHelper.language
    var sortParam: String = SharedPreferencesHelper.sortParam
    var isSortOrderAscending: Boolean = SharedPreferencesHelper.isSortOrderAscending
    var swipeRefresh: Boolean = SharedPreferencesHelper.swipeRefresh
    val settingsForm: LiveData<Int?> = _settingsForm
    val settingsLoading: LiveData<Boolean> = _settingsLoading
    val settingsError: LiveData<ErrorResponse?> = _settingsError
    //endregion

    //region Public methods
    fun logout() {

//        _settingsLoading.value = true
//        userRepository.logout()
//        SharedPreferencesHelper.removePassword()
//        resetDatabase()

        SharedPreferencesHelper.logout()
        _settingsError.value = null
    }

    fun save(
        newPassword: String,
        newLanguage: String,
        newSortParam: String,
        newIsSortOrderAscending: Boolean,
        newSwipeRefresh: Boolean,
        themeMode: Int
    ) {

        val changePassword = newPassword != userData.password
        val changeLanguage = newLanguage != language
        val changeSortParam = newSortParam != sortParam
        val changeIsSortDescending = newIsSortOrderAscending != isSortOrderAscending
        val changeSwipeRefresh = newSwipeRefresh != swipeRefresh
        val changeThemeMode = themeMode != SharedPreferencesHelper.themeMode

        if (changePassword) {
            _settingsLoading.value = true
            userRepository.updatePassword(newPassword, {

                SharedPreferencesHelper.storePassword(newPassword)
                val userData = SharedPreferencesHelper.userData
                userRepository.login(userData.username, userData.password, {

                    SharedPreferencesHelper.credentials = AuthData(it)
                    _settingsLoading.value = false
                    if (changeLanguage || changeSortParam || changeIsSortDescending) {
                        _settingsError.value = null
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

        if (changeIsSortDescending) {

            SharedPreferencesHelper.isSortOrderAscending = newIsSortOrderAscending
            isSortOrderAscending = newIsSortOrderAscending
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

        if (!changePassword && (changeLanguage || changeSortParam || changeIsSortDescending)) {
            _settingsError.value = null
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

    fun profileDataChanged(password: String) {

        var passwordError: Int? = null
        if (!Constants.isPasswordValid(password)) {
            passwordError = R.string.invalid_password
        }
        _settingsForm.value = passwordError
    }

    fun importData(jsonData: String) {

        val json = JsonParser.parseString(jsonData)
        val jsonGames = json.asJsonObject["games"].toString()
        val jsonSagas = json.asJsonObject["sagas"].toString()

        var listType = object : TypeToken<List<GameResponse?>?>() {}.type
        val games = Gson().fromJson<List<GameResponse?>>(jsonGames, listType).mapNotNull { it }
        listType = object : TypeToken<List<SagaResponse?>?>() {}.type
        val sagas = Gson().fromJson<List<SagaResponse?>>(jsonSagas, listType).mapNotNull { it }

        for (game in games) {
            gameRepository.insertGameDatabase(game)
        }
        for (saga in sagas) {
            sagaRepository.insertSagaDatabase(saga)
        }
    }

    fun getDataToExport(): String {

        val data = mapOf(
            "games" to gameRepository.getGamesDatabase(),
            "sagas" to sagaRepository.getSagasDatabase()
        )
        return Gson().toJson(data)
    }
    //endregion

    //region Private methods
    private fun resetDatabase() {

        gameRepository.resetTable()
        sagaRepository.resetTable()

        _settingsLoading.value = false
        _settingsError.value = null
    }
    //endregion
}