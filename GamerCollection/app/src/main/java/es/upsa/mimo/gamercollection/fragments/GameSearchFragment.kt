package es.upsa.mimo.gamercollection.fragments

import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.activities.GameDetailActivity
import es.upsa.mimo.gamercollection.adapters.GamesAdapter
import es.upsa.mimo.gamercollection.adapters.OnItemClickListener
import es.upsa.mimo.gamercollection.base.BindingFragment
import es.upsa.mimo.gamercollection.databinding.FragmentGameSearchBinding
import es.upsa.mimo.gamercollection.fragments.GamesFragment.ScrollPosition
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.viewmodelfactories.GameSearchViewModelFactory
import es.upsa.mimo.gamercollection.viewmodels.GameSearchViewModel

class GameSearchFragment : BindingFragment<FragmentGameSearchBinding>(), OnItemClickListener {

    //region Private properties
    private lateinit var viewModel: GameSearchViewModel
    private lateinit var gamesAdapter: GamesAdapter
    private val scrollPosition = ObservableField<ScrollPosition>()
    //endregion

    //region Lifecycle methods
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeUI()
    }

    override fun onResume() {
        super.onResume()
        searchView?.setQuery(Constants.EMPTY_VALUE, false)
    }

    override fun onStop() {
        super.onStop()
        searchView?.setQuery(Constants.EMPTY_VALUE, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        menu.clear()
        inflater.inflate(R.menu.game_search_toolbar_menu, menu)
        setupSearchView(menu)
    }
    //endregion

    //region Interface methods
    override fun onItemClick(id: Int) {

        val params = mapOf(Constants.GAME_ID to id, Constants.IS_RAWG_GAME to true)
        launchActivityWithExtras(GameDetailActivity::class.java, params)
    }

    override fun onSubItemClick(id: Int) {
    }

    override fun onLoadMoreItemsClick() {

        viewModel.loadGames()
        scrollPosition.set(ScrollPosition.MIDDLE)
    }
    //endregion

    //region Public methods
    fun goToStartEndList(view: View) {

        with(binding) {
            when (view) {
                floatingActionButtonStartList -> {

                    recyclerViewGames.scrollToPosition(0)
                    scrollPosition.set(ScrollPosition.TOP)
                }
                floatingActionButtonEndList -> {

                    val position: Int = gamesAdapter.itemCount - 1
                    recyclerViewGames.scrollToPosition(position)
                    scrollPosition.set(ScrollPosition.END)
                }
            }
        }
    }
    //endregion

    //region Private methods
    private fun initializeUI() {

        val application = activity?.application
        viewModel = ViewModelProvider(
            this,
            GameSearchViewModelFactory(application)
        ).get(GameSearchViewModel::class.java)
        setupBindings()

        with(binding) {

            swipeRefreshLayout.apply {
                isEnabled = this@GameSearchFragment.viewModel.swipeRefresh
                setColorSchemeResources(R.color.colorPrimary)
                setProgressBackgroundColorSchemeResource(R.color.colorSecondary)
                setOnRefreshListener {

                    this@GameSearchFragment.viewModel.query = null
                    reset()
                }
            }

            gamesAdapter = GamesAdapter(
                this@GameSearchFragment.viewModel.games.value ?: listOf(),
                this@GameSearchFragment.viewModel.platforms,
                null,
                this@GameSearchFragment
            )
            recyclerViewGames.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = gamesAdapter
                addOnScrollListener(object : RecyclerView.OnScrollListener() {

                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        super.onScrollStateChanged(recyclerView, newState)

                        scrollPosition.set(
                            if (!recyclerView.canScrollVertically(-1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                                ScrollPosition.TOP
                            } else if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                                ScrollPosition.END
                            } else {
                                ScrollPosition.MIDDLE
                            }
                        )
                    }
                })
            }

            fragment = this@GameSearchFragment
            viewModel = this@GameSearchFragment.viewModel
            lifecycleOwner = this@GameSearchFragment
            position = scrollPosition
        }

        scrollPosition.set(ScrollPosition.TOP)
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

        viewModel.gamesCount.observe(viewLifecycleOwner, {
            setTitle(it)
        })

        viewModel.scrollPosition.observe(viewLifecycleOwner, {
            scrollPosition.set(it)
        })
    }

    private fun setTitle(gamesCount: Int) {

        val title = resources.getQuantityString(
            R.plurals.games_number_title,
            gamesCount,
            Constants.getFormattedNumber(gamesCount)
        )
        (activity as AppCompatActivity?)?.supportActionBar?.title = title
    }

    private fun reset() {

        gamesAdapter.resetList()
        viewModel.resetPage()
        viewModel.loadGames()
    }

    private fun setupSearchView(menu: Menu) {

        val menuItem = menu.findItem(R.id.action_search_rawg)
        searchView = menuItem.actionView as SearchView
        searchView?.let { searchView ->

            searchView.queryHint = resources.getString(R.string.search_games)
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

                override fun onQueryTextChange(newText: String): Boolean {
                    return true
                }

                override fun onQueryTextSubmit(query: String): Boolean {

                    searchGames(query)
                    menuItem.collapseActionView()
                    return true
                }
            })
        }
        setupSearchView(viewModel.query ?: Constants.EMPTY_VALUE)
    }

    private fun searchGames(query: String) {

        viewModel.query = query
        reset()
        Constants.hideSoftKeyboard(requireActivity())
    }
    //endregion
}