package es.upsa.mimo.gamercollection.presentation.gamedetail

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import es.upsa.mimo.gamercollection.domain.model.Game
import es.upsa.mimo.gamercollection.presentation.gamedetail.gamedata.GameDataFragment
import es.upsa.mimo.gamercollection.presentation.gamedetail.gamesongs.GameSongsFragment

class GameDetailPagerAdapter(
    activity: FragmentActivity,
    private val itemsCount: Int,
    private val currentGame: Game?,
) : FragmentStateAdapter(activity) {

    //region Private properties
    private var gameDataFragment: GameDataFragment? = null
    private var gameSongsFragment: GameSongsFragment? = null
    private var enabled = false
    //endregion

    //region Lifecycle methods
    override fun getItemCount(): Int = itemsCount

    override fun createFragment(position: Int): Fragment {
        val fragment = if (position == 0) {
            gameDataFragment = GameDataFragment(currentGame, enabled)
            gameDataFragment
        } else {
            gameSongsFragment = GameSongsFragment(currentGame, enabled)
            gameSongsFragment
        }
        return fragment ?: Fragment()
    }
    //endregion

    //region Public methods
    fun showData(game: Game?) {
        gameDataFragment?.showData(game)
    }

    fun setEdition(editable: Boolean) {
        enabled = editable
        gameDataFragment?.setEdition(editable)
        gameSongsFragment?.setEdition(editable)
    }

    fun getGameData(): Game? {
        val game = gameDataFragment?.getGameData()?.copy(
            songs = gameSongsFragment?.getSongs() ?: currentGame?.songs ?: emptyList(),
        )
        return game
    }
    //endregion
}