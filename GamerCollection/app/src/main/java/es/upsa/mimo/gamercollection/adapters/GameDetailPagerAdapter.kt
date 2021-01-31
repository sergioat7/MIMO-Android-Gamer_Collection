package es.upsa.mimo.gamercollection.adapters

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import es.upsa.mimo.gamercollection.fragments.GameDataFragment
import es.upsa.mimo.gamercollection.fragments.GameSongsFragment
import es.upsa.mimo.gamercollection.models.responses.GameResponse

class GameDetailPagerAdapter(
    activity: AppCompatActivity,
    private val itemsCount: Int,
    private val currentGame: GameResponse?
): FragmentStateAdapter(activity) {

    //MARK: - Private properties

    private var gameDataFragment: GameDataFragment? = null
    private var gameSongsFragment: GameSongsFragment? = null
    private var enabled = false

    //MARK: - Lifecycle methods

    override fun getItemCount(): Int {
        return itemsCount
    }

    override fun createFragment(position: Int): Fragment {

        val fragment = if (position == 0) {
            gameDataFragment = GameDataFragment(currentGame)
            gameDataFragment
        } else {
            gameSongsFragment = GameSongsFragment(currentGame, enabled)
            gameSongsFragment
        }
        return fragment ?: Fragment()
    }

    // MARK: Public methods

    fun enableEdition(enable: Boolean) {

        this.enabled = enable
        gameSongsFragment?.enableEdition(enable)
    }

    fun showData(game: GameResponse?, enabled: Boolean) {
        gameDataFragment?.showData(game, enabled)
    }

    fun getGameData(): GameResponse? {

        val game = gameDataFragment?.getGameData()
        if (game != null) {
            game.songs = gameSongsFragment?.getSongs() ?: currentGame?.songs ?: ArrayList()
        }
        return game
    }
}