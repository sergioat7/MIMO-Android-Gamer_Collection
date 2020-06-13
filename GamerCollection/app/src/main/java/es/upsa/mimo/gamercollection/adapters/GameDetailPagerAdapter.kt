package es.upsa.mimo.gamercollection.adapters

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import es.upsa.mimo.gamercollection.fragments.GameDetailFragment
import es.upsa.mimo.gamercollection.fragments.GameSongsFragment
import es.upsa.mimo.gamercollection.models.GameResponse

class GameDetailPagerAdapter(
    activity: AppCompatActivity,
    private val itemsCount: Int,
    private val currentGame: GameResponse?
): FragmentStateAdapter(activity) {

    private var gameDetailFragment: GameDetailFragment? = null
    private var gameSongsFragment: GameSongsFragment? = null
    private var enabled = false

    override fun getItemCount(): Int {
        return itemsCount
    }

    override fun createFragment(position: Int): Fragment {

        return if (position == 0) {
            gameDetailFragment = GameDetailFragment(currentGame)
            gameDetailFragment!!
        } else {
            gameSongsFragment = GameSongsFragment(currentGame, enabled)
            gameSongsFragment!!
        }
    }

    fun enableEdition(enable: Boolean) {
        this.enabled = enable
        gameSongsFragment?.enableEdition(enable)
    }

    fun showData(game: GameResponse?, enabled: Boolean) {
        gameDetailFragment?.showData(game, enabled)
    }

    fun getGameData(): GameResponse? {

        val game = gameDetailFragment?.getGameData()
        if (game != null) {
            game.songs = gameSongsFragment?.getSongs() ?: currentGame?.songs ?: ArrayList()
        }
        return game
    }
}