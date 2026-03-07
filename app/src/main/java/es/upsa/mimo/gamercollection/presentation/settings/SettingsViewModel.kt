package es.upsa.mimo.gamercollection.presentation.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.data.local.SharedPreferencesHelper
import es.upsa.mimo.gamercollection.data.local.model.AuthData
import es.upsa.mimo.gamercollection.data.local.model.UserData
import es.upsa.mimo.gamercollection.data.remote.model.ExportData
import es.upsa.mimo.gamercollection.data.remote.model.GameResponse
import es.upsa.mimo.gamercollection.data.remote.model.SagaResponse
import es.upsa.mimo.gamercollection.domain.GameRepository
import es.upsa.mimo.gamercollection.domain.SagaRepository
import es.upsa.mimo.gamercollection.domain.SongRepository
import es.upsa.mimo.gamercollection.domain.UserRepository
import es.upsa.mimo.gamercollection.domain.model.ErrorModel
import es.upsa.mimo.gamercollection.domain.toDomain
import es.upsa.mimo.gamercollection.domain.toRemoteData
import es.upsa.mimo.gamercollection.utils.Constants
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val sagaRepository: SagaRepository,
    private val songRepository: SongRepository,
    private val userRepository: UserRepository,
    private val preferences: SharedPreferencesHelper,
) : ViewModel() {

    //region Private properties
    private val _settingsForm = MutableStateFlow<Int?>(null)
    private val _settingsLoading = MutableStateFlow<Boolean?>(null)
    private val _settingsError = MutableStateFlow<ErrorModel?>(null)
    private val _logOut = MutableStateFlow<Boolean?>(null)
    //endregion

    //region Public properties
    val userData: UserData
        get() = preferences.userData
    val language: String = preferences.language
    var sortParam: String = preferences.sortParam
    var isSortOrderAscending: Boolean = preferences.isSortOrderAscending
    var swipeRefresh: Boolean = preferences.swipeRefresh
    val settingsForm: StateFlow<Int?> = _settingsForm
    val settingsLoading: StateFlow<Boolean?> = _settingsLoading
    val settingsError: StateFlow<ErrorModel?> = _settingsError
    val logOut: StateFlow<Boolean?> = _logOut
    //endregion

    //region Public methods
    fun logout() {
        preferences.logout()
        _logOut.value = true
    }

    fun save(
        newPassword: String,
        newLanguage: String,
        newSortParam: String,
        newIsSortOrderAscending: Boolean,
        newSwipeRefresh: Boolean,
        themeMode: Int,
    ) {
        val changePassword = newPassword != userData.password
        val changeLanguage = newLanguage != language
        val changeSortParam = newSortParam != sortParam
        val changeIsSortDescending = newIsSortOrderAscending != isSortOrderAscending
        val changeSwipeRefresh = newSwipeRefresh != swipeRefresh
        val changeThemeMode = themeMode != preferences.themeMode

        if (changePassword) {
            _settingsLoading.value = true
            _settingsError.value = null
            preferences.storePassword(newPassword)
            val userData = preferences.userData
            userRepository.login(userData.username, userData.password, {
                preferences.credentials = AuthData(it)
                _settingsLoading.value = false
                if (changeLanguage || changeSortParam || changeIsSortDescending) {
                    _logOut.value = true
                }
            }, {
                _settingsError.value = it
            })
        }

        if (changeLanguage) {
            preferences.language = newLanguage
        }

        if (changeSortParam) {
            preferences.sortParam = newSortParam
            sortParam = newSortParam
        }

        if (changeIsSortDescending) {
            preferences.isSortOrderAscending = newIsSortOrderAscending
            isSortOrderAscending = newIsSortOrderAscending
        }

        if (changeSwipeRefresh) {
            preferences.swipeRefresh = newSwipeRefresh
            swipeRefresh = newSwipeRefresh
        }

        if (changeThemeMode) {
            preferences.themeMode = themeMode
            when (themeMode) {
                1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                else -> AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM,
                )
            }
        }

        if (!changePassword && (changeLanguage || changeSortParam || changeIsSortDescending)) {
            _logOut.value = true
        }
    }

    fun deleteUser() {
        preferences.removeUserData()
        preferences.removeCredentials()
        resetDatabase()
    }

    fun profileDataChanged(password: String) {
        var passwordError: Int? = null
        if (!Constants.isPasswordValid(password)) {
            passwordError = R.string.invalid_password
        }
        _settingsForm.value = passwordError
    }

    fun importData(jsonData: String) {
        val json = Json.parseToJsonElement(jsonData)
        val jsonGames = json.jsonObject["games"].toString()
        val jsonSagas = json.jsonObject["sagas"].toString()

        val games = Json.decodeFromString<List<GameResponse?>>(jsonGames).mapNotNull { it }
        val sagas = Json.decodeFromString<List<SagaResponse?>>(jsonSagas).mapNotNull { it }

        viewModelScope.launch {
            for (game in games) {
                gameRepository.insertGameDatabase(game.toDomain())
                for (song in game.songs) {
                    songRepository.insertSongDatabase(song.toDomain())
                }
            }
            for (saga in sagas) {
                sagaRepository.insertSagaDatabase(saga.toDomain())
            }
        }
    }

    fun getDataToExport(): String? = try {
        lateinit var data: ExportData
        runBlocking {
            val games = gameRepository.getGamesDatabase().map { it.toRemoteData() }.also {
                it.forEach { game ->
                    game.saga = game.saga?.copy(games = emptyList())
                }
            }
            val sagas = sagaRepository.getSagasDatabase().map { it.toRemoteData() }.also {
                it.forEach { saga ->
                    saga.games.forEach { game ->
                        game.saga = game.saga?.copy(games = emptyList())
                    }
                }
            }
            data = ExportData(games, sagas)
        }
        Json.encodeToString(data)
    } catch (_: Exception) {
        null
    }
    //endregion

    //region Private methods
    private fun resetDatabase() {
        viewModelScope.launch {
            gameRepository.resetTable()
            sagaRepository.resetTable()

            _settingsLoading.value = false
            _logOut.value = true
        }
    }
    //endregion
}