package es.upsa.mimo.gamercollection.fragments

import android.os.Bundle
import android.view.*
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.activities.GameDetailActivity
import es.upsa.mimo.gamercollection.fragments.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_games.*

class GamesFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_games, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeUI()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.games_toolbar_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId) {
            R.id.action_synchronize -> {
                openSyncPopup()
                return true
            }
            R.id.action_filter -> {
                filter()
                return true
            }
            R.id.action_add -> {
                add()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //MARK: - Private functions

    private fun initializeUI() {

        text_view_games_number.text = resources.getString(R.string.GAMES_NUMBER_TITLE, 0)

        button_sort.setOnClickListener { sort() }

        button_pending.text = resources.getString(R.string.GAMES_FILTER_BUTTON_TITLE_PENDING, 0)
        button_pending.setOnClickListener {
            it.isSelected = !it.isSelected
            button_in_progress.isSelected = false
            button_finished.isSelected = false
            showPendingGames()
        }
        button_in_progress.text = resources.getString(R.string.GAMES_FILTER_BUTTON_TITLE_IN_PROGRESS, 0)
        button_in_progress.setOnClickListener {

            button_pending.isSelected = false
            it.isSelected = !it.isSelected
            button_finished.isSelected = false
            showInProgressGames()
        }
        button_finished.text = resources.getString(R.string.GAMES_FILTER_BUTTON_TITLE_FINISHED, 0)
        button_finished.setOnClickListener {

            button_pending.isSelected = false
            button_in_progress.isSelected = false
            it.isSelected = !it.isSelected
            showFinishedGames()
        }
    }

    private fun filter() {
        //TODO
    }

    private fun add() {
        launchActivity(GameDetailActivity::class.java)
    }

    private fun sort() {
        //TODO
    }

    private fun showPendingGames() {
        //TODO
    }

    private fun showInProgressGames() {
        //TODO
    }

    private fun showFinishedGames() {
        //TODO
    }
}
