package es.upsa.mimo.gamercollection.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.adapters.SongsAdapter
import es.upsa.mimo.gamercollection.fragments.base.BaseFragment
import es.upsa.mimo.gamercollection.models.GameResponse
import kotlinx.android.synthetic.main.fragment_game_songs.*

class GameSongsFragment : BaseFragment(), SongsAdapter.OnItemClickListener {

    private var currentGame: GameResponse? = null
    private var enable = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        currentGame = Gson().fromJson(arguments?.getString("game"), GameResponse::class.java)
        return inflater.inflate(R.layout.fragment_game_songs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeUI()
    }

    override fun onItemClick(songId: Int) {

//        currentGame?.let { game ->
//
//            showLoading()
//            songAPIClient.deleteSong(game.id, songId, {
//                updateData()
//            }, {
//                manageError(it)
//            })
//        }
    }

    fun enableEdition(enable: Boolean) {

        this.enable = enable
        val adapter = recycler_view_songs?.adapter as? SongsAdapter
        if (adapter != null) {
            adapter.editable = enable
            adapter.notifyDataSetChanged()
        }
        button_add_song?.visibility = if(enable) View.VISIBLE else View.GONE
    }

    // MARK: Private functions

    private fun initializeUI() {

        recycler_view_songs.layoutManager = LinearLayoutManager(requireContext())
        currentGame?.let {
            recycler_view_songs.adapter = SongsAdapter(it.songs, enable, this)
        }
    }

    private fun updateData() {

//        currentGame?.let { game ->
//            gameAPIClient.getGame(game.id, {
//                gameRepository.updateGame(it)
//
//                currentGame = it
//                showData(currentGame)
//                hideLoading()
//            }, {
//                manageError(it)
//            })
//        }
    }
}