package es.upsa.mimo.gamercollection.fragments

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.activities.GameDetailActivity
import es.upsa.mimo.gamercollection.adapters.GamesAdapter
import es.upsa.mimo.gamercollection.adapters.OnItemClickListener
import es.upsa.mimo.gamercollection.fragments.base.BaseFragment
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.viewmodelfactories.GameSearchViewModelFactory
import es.upsa.mimo.gamercollection.viewmodels.GameSearchViewModel
import kotlinx.android.synthetic.main.fragment_game_search.*

class GameSearchFragment : BaseFragment(), OnItemClickListener {

    //MARK: - Private properties

    private lateinit var viewModel: GameSearchViewModel
    private lateinit var gamesAdapter: GamesAdapter
    private val scrollPosition = MutableLiveData<ScrollPosition>()

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

    //MARK: - Interface methods

    override fun onItemClick(id: Int) {

        val params = mapOf(Constants.GAME_ID to id)
        launchActivityWithExtras(GameDetailActivity::class.java, params)
    }

    override fun onSubItemClick(id: Int) {}

    //MARK: - Private methods

    private fun initializeUI() {

        val application = activity?.application
        viewModel = ViewModelProvider(this, GameSearchViewModelFactory(application)).get(GameSearchViewModel::class.java)
        setupBindings()

        swipe_refresh_layout.isEnabled = viewModel.swipeRefresh
        swipe_refresh_layout.setColorSchemeResources(R.color.colorPrimary)
        swipe_refresh_layout.setProgressBackgroundColorSchemeResource(R.color.colorSecondary)
        swipe_refresh_layout.setOnRefreshListener {

            viewModel.resetPage()
            viewModel.loadGames()
        }

        recycler_view_games.layoutManager = LinearLayoutManager(requireContext())
        gamesAdapter = GamesAdapter(
            viewModel.games.value ?: listOf(),
            viewModel.platforms,
            viewModel.states,
            null,
            requireContext(),
            this
        )
        recycler_view_games.adapter = gamesAdapter
        recycler_view_games.addOnScrollListener(object: RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                scrollPosition.value =
                    if (!recyclerView.canScrollVertically(-1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                        ScrollPosition.TOP
                    } else if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                        ScrollPosition.END
                    } else {
                        ScrollPosition.MIDDLE
                    }
            }
        })

        scrollPosition.value = ScrollPosition.TOP

        floating_action_button_start_list.setOnClickListener {

            recycler_view_games.scrollToPosition(0)
            scrollPosition.value = ScrollPosition.TOP
        }

        floating_action_button_end_list.setOnClickListener {

            val position: Int = gamesAdapter.itemCount - 1
            recycler_view_games.scrollToPosition(position)
            scrollPosition.value = ScrollPosition.END
        }
    }

    private fun setupBindings() {

        viewModel.gamesLoading.observe(viewLifecycleOwner, { isLoading ->

            if (isLoading) {
                showLoading()
            } else {
                hideLoading()
            }
        })

        viewModel.gamesError.observe(viewLifecycleOwner, { error ->
            manageError(error)
        })

        viewModel.games.observe(viewLifecycleOwner, {

            gamesAdapter.setGames(it)
            layout_empty_list.visibility = if (it.isNotEmpty()) View.GONE else View.VISIBLE
            swipe_refresh_layout.visibility = if (it.isNotEmpty()) View.VISIBLE else View.GONE
            scrollPosition.value = if (it.isNotEmpty()) scrollPosition.value else ScrollPosition.NONE
        })

        viewModel.gamesCount.observe(viewLifecycleOwner, {
            setTitle(it)
        })

        viewModel.scrollPosition.observe(viewLifecycleOwner, {
            scrollPosition.value = it
        })

        scrollPosition.observe(viewLifecycleOwner, {

            floating_action_button_start_list.visibility = if (it == ScrollPosition.TOP || it == ScrollPosition.NONE) View.GONE else View.VISIBLE
            floating_action_button_end_list.visibility = if (it == ScrollPosition.END || it == ScrollPosition.NONE) View.GONE else View.VISIBLE
        })
    }

    private fun setTitle(gamesCount: Int) {

        val title = resources.getQuantityString(R.plurals.games_number_title, gamesCount, gamesCount)
        (activity as AppCompatActivity?)?.supportActionBar?.title = title
    }
}