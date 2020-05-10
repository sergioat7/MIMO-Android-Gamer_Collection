package es.upsa.mimo.gamercollection.activities

import android.os.Bundle
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.activities.base.BaseActivity
import es.upsa.mimo.gamercollection.fragments.GameDetailFragment

class GameDetailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTitle(R.string.GAME_DETAIL)
        val gameId = intent.getIntExtra("gameId", 0)
        setContentView(R.layout.activity_game_detail)

        val gameDetailFragment = GameDetailFragment()
        if (gameId > 0) {
            val bundle = Bundle()
            bundle.putInt("gameId", gameId)
            gameDetailFragment.arguments = bundle
        }
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.game_detail_fragment_placeholder, gameDetailFragment)
        transaction.commit()
    }
}
