package es.upsa.mimo.gamercollection.fragments

import android.os.Bundle
import android.view.*
import androidx.lifecycle.ViewModelProvider
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.fragments.base.BaseFragment
import es.upsa.mimo.gamercollection.viewmodels.GameSearchViewModel

class GameSearchFragment : BaseFragment() {

    //MARK: - Private properties

    private lateinit var viewModel: GameSearchViewModel

    // MARK: - Lifecycle methods

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_game_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeUI()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        menu.clear()
        inflater.inflate(R.menu.game_search_toolbar_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId) {
            R.id.action_search -> {

                //TODO: set search action
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //MARK: - Private methods

    private fun initializeUI() {

        viewModel = ViewModelProvider(this).get(GameSearchViewModel::class.java)
        setupBindings()
        // TODO: Use the ViewModel
    }

    private fun setupBindings() {
        //TODO: setup bindings
    }
}