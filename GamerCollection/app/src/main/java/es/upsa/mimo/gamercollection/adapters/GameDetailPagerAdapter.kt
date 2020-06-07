package es.upsa.mimo.gamercollection.adapters

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.gson.Gson
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

    override fun getItemCount(): Int {
        return itemsCount
    }

    override fun createFragment(position: Int): Fragment {

        val args = Bundle()
        args.putString("game", Gson().toJson(currentGame))
        return if (position == 0) {
            gameDetailFragment = GameDetailFragment()
            gameDetailFragment!!.arguments = args
            gameDetailFragment!!
        } else {
            gameSongsFragment = GameSongsFragment()
            gameSongsFragment!!.arguments = args
            gameSongsFragment!!
        }
    }

    fun enableEdition(enable: Boolean) {

        gameDetailFragment?.enableEdition(enable)
        gameSongsFragment?.enableEdition(enable)
    }

    fun showData(game: GameResponse?) {

        gameDetailFragment?.showData(game)
    }

    fun getGameData(): GameResponse? {

        val game = gameDetailFragment?.getGameData()
        if (game != null) {
            game.songs = gameSongsFragment?.getSongs() ?: currentGame?.songs ?: ArrayList()
        }
        return game
    }
}