package es.upsa.mimo.gamercollection.viewmodels

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.NumberPicker
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.models.FilterModel
import es.upsa.mimo.gamercollection.models.responses.ErrorResponse
import es.upsa.mimo.gamercollection.models.responses.GameResponse
import es.upsa.mimo.gamercollection.models.responses.PlatformResponse
import es.upsa.mimo.gamercollection.models.responses.StateResponse
import es.upsa.mimo.gamercollection.repositories.GameRepository
import es.upsa.mimo.gamercollection.repositories.PlatformRepository
import es.upsa.mimo.gamercollection.repositories.StateRepository
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler
import javax.inject.Inject

class GamesViewModel @Inject constructor(
    private val sharedPreferencesHandler: SharedPreferencesHandler,
    private val gameRepository: GameRepository,
    private val platformRepository: PlatformRepository,
    private val stateRepository: StateRepository
) : ViewModel() {

    //MARK: - Private properties

    private val _gamesLoading = MutableLiveData<Boolean>()
    private val _gamesError = MutableLiveData<ErrorResponse>()
    private val _games = MutableLiveData<List<GameResponse>>()
    private val _gamesCount = MutableLiveData<List<GameResponse>>()
    private var sortKey: String = sharedPreferencesHandler.getSortingKey()
    private var sortAscending = true

    //MARK: - Public properties

    val language: String
        get() = sharedPreferencesHandler.getLanguage()
    val swipeRefresh: Boolean
        get() = sharedPreferencesHandler.getSwipeRefresh()
    val platforms: List<PlatformResponse>
        get() = platformRepository.getPlatformsDatabase()
    val states: List<StateResponse>
        get() = stateRepository.getStatesDatabase()
    val gamesLoading: LiveData<Boolean> = _gamesLoading
    val gamesError: LiveData<ErrorResponse> = _gamesError
    val games: LiveData<List<GameResponse>> = _games
    val gamesCount: LiveData<List<GameResponse>> = _gamesCount
    var state: String? = null
    var filters: FilterModel? = null

    //MARK: - Public methods

    fun loadGames() {

        _gamesLoading.value = true
        gameRepository.loadGames({

            resetProperties()
            getGames()
            _gamesLoading.value = false
        }, {

            _gamesLoading.value = true
            _gamesError.value = it
        })
    }

    fun getGames() {

        val games = gameRepository.getGamesDatabase(
            state,
            filters,
            sortKey,
            sortAscending
        )
        _games.value = games

        if (_gamesCount.value == null) {
            _gamesCount.value = games
        }
    }

    fun sortGames(context: Context, resources: Resources) {

        val sortingKeys = resources.getStringArray(R.array.sorting_keys_ids)
        val sortingValues = resources.getStringArray(R.array.sorting_keys)

        val dialogView = LinearLayout(context)
        dialogView.orientation = LinearLayout.HORIZONTAL

        val ascendingPicker = getPicker(
            arrayOf(
                resources.getString(R.string.ascending),
                resources.getString(R.string.descending)
            ), context
        )
        ascendingPicker.value = if (sortAscending) 0 else 1
        val ascendingPickerParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        ascendingPickerParams.weight = 1f

        val sortKeysPicker = getPicker(sortingValues, context)
        sortKeysPicker.value = sortingKeys.indexOf(sortKey)
        val sortKeysPickerParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        sortKeysPickerParams.weight = 1f

        val params = LinearLayout.LayoutParams(50, 50)
        params.gravity = Gravity.CENTER

        dialogView.layoutParams = params
        dialogView.addView(sortKeysPicker, sortKeysPickerParams)
        dialogView.addView(ascendingPicker, ascendingPickerParams)

        AlertDialog.Builder(context)
            .setTitle(resources.getString(R.string.sort_title))
            .setView(dialogView)
            .setCancelable(false)
            .setPositiveButton(resources.getString(R.string.accept)) { dialog, _ ->

                sortKey = sortingKeys[sortKeysPicker.value]
                sortAscending = ascendingPicker.value == 0
                getGames()
                dialog.dismiss()
            }
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    fun isNotificationLaunched(gameId: Int): Boolean {
        return sharedPreferencesHandler.notificationLaunched(gameId)
    }

    fun setNotificationLaunched(gameId: Int, value: Boolean) {
        sharedPreferencesHandler.setNotificationLaunched(gameId, value)
    }

    // MARK: Private methods

    private fun getPicker(values: Array<String>, context: Context): NumberPicker {

        val picker = NumberPicker(context)
        picker.minValue = 0
        picker.maxValue = values.size - 1
        picker.wrapSelectorWheel = true
        picker.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        picker.displayedValues = values
        return picker
    }

    private fun resetProperties() {

        state = null
        sortKey = sharedPreferencesHandler.getSortingKey()
        sortAscending = true
        filters = null
    }
}