package es.upsa.mimo.gamercollection.ui.gamedetail

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import es.upsa.mimo.gamercollection.models.GameResponse
import es.upsa.mimo.gamercollection.ui.gamedetail.gamedata.GameDataFragment
import es.upsa.mimo.gamercollection.ui.gamedetail.gamesongs.GameSongsFragment

class GameDetailPagerAdapter(
    activity: FragmentActivity,
    private val itemsCount: Int,
    private val currentGame: GameResponse?
) : FragmentStateAdapter(activity) {

    //region Private properties
    private var gameDataFragment: GameDataFragment? = null
    private var gameSongsFragment: GameSongsFragment? = null
    private var enabled = false
    //endregion

    //region Lifecycle methods
    override fun getItemCount(): Int {
        return itemsCount
    }

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
    fun showData(game: GameResponse?) {
        gameDataFragment?.showData(game)
    }

    fun setEdition(editable: Boolean) {

        enabled = editable
        gameDataFragment?.setEdition(editable)
        gameSongsFragment?.setEdition(editable)
    }

    fun getGameData(): GameResponse? {

        val game = gameDataFragment?.getGameData()
        if (game != null) {
            val songs = gameSongsFragment?.getSongs() ?: currentGame?.songs
            game.songs = songs?.toMutableList() ?: mutableListOf()
        }
        return game
    }
    //endregion
}