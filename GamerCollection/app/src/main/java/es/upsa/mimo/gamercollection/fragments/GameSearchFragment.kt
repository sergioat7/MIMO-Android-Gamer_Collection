package es.upsa.mimo.gamercollection.fragments

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.adapters.GamesAdapter
import es.upsa.mimo.gamercollection.adapters.OnItemClickListener
import es.upsa.mimo.gamercollection.base.BindingFragment
import es.upsa.mimo.gamercollection.databinding.FragmentGameSearchBinding
import es.upsa.mimo.gamercollection.extensions.hideSoftKeyboard
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.utils.ScrollPosition
import es.upsa.mimo.gamercollection.utils.StatusBarStyle
import es.upsa.mimo.gamercollection.viewmodelfactories.GameSearchViewModelFactory
import es.upsa.mimo.gamercollection.viewmodels.GameSearchViewModel
import kotlin.math.max
import kotlin.math.min

class GameSearchFragment : BindingFragment<FragmentGameSearchBinding>(), OnItemClickListener {

    //region Protected properties
    override val statusBarStyle = StatusBarStyle.SECONDARY
    override val hasOptionsMenu = true
    //endregion

    //region Private properties
    private lateinit var viewModel: GameSearchViewModel
    private lateinit var gamesAdapter: GamesAdapter
    private val scrollPosition = ObservableField<ScrollPosition>()
    //endregion

    //region Lifecycle methods
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar = binding.toolbar
        initializeUi()
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

        val action = GameSearchFragmentDirections.actionSearchFragmentToGameDetailFragment(id, true)
        findNavController().navigate(action)
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

    //region Protected methods
    override fun initializeUi() {
        super.initializeUi()

        val application = activity?.application
        viewModel = ViewModelProvider(
            this,
            GameSearchViewModelFactory(application)
        )[GameSearchViewModel::class.java]
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
                null,
                this@GameSearchFragment
            )
            recyclerViewGames.apply {
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
                ItemTouchHelper(SwipeController()).attachToRecyclerView(this)
            }

            fragment = this@GameSearchFragment
            viewModel = this@GameSearchFragment.viewModel
            lifecycleOwner = this@GameSearchFragment
            position = scrollPosition
        }

        scrollPosition.set(ScrollPosition.TOP)
    }
    //endregion

    //region Protected methods
    private fun setupBindings() {

        viewModel.gamesLoading.observe(viewLifecycleOwner) { isLoading ->

            if (isLoading) {
                showLoading()
            } else {
                hideLoading()
            }
        }

        viewModel.gamesError.observe(viewLifecycleOwner) { error ->
            manageError(error)
        }

        viewModel.scrollPosition.observe(viewLifecycleOwner) {
            scrollPosition.set(it)
        }
    }

    private fun reset() {

        gamesAdapter.resetList()
        viewModel.resetPage()
        viewModel.loadGames()
    }

    private fun setupSearchView(menu: Menu) {

        val menuItem = menu.findItem(R.id.action_search_rawg)
        this.searchView = menuItem.actionView as SearchView
        this.searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {

                searchGames(query)
                menuItem.collapseActionView()
                return true
            }
        })
        setupSearchView(R.color.textSecondary, Constants.EMPTY_VALUE, R.string.search_games)
    }

    private fun searchGames(query: String) {

        viewModel.query = query
        reset()
        requireActivity().hideSoftKeyboard()
    }
    //endregion

    //region SwipeController
    inner class SwipeController :
        ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

        private val paint = Paint()

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ) = false

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

            val position = viewHolder.adapterPosition
//            viewModel.addGame(position)//TODO: implement
            gamesAdapter.notifyItemChanged(position)
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {

            var x = dX
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                val itemView = viewHolder.itemView
                val context = recyclerView.context

                val height = itemView.bottom - itemView.top
                val width = height / 3
                val maxX = itemView.width.toFloat() * 0.6F

                paint.color = ContextCompat.getColor(context, R.color.colorFinished)
                val icon = ContextCompat.getDrawable(context, R.drawable.ic_add_game)

                when {
                    dX < 0 -> {// Swiping to the left
                        val background = RectF(
                            itemView.right.toFloat() + dX,
                            itemView.top.toFloat(),
                            itemView.right.toFloat(),
                            itemView.bottom.toFloat()
                        )
                        c.drawRect(background, paint)

                        icon?.setBounds(
                            itemView.right - 2 * width,
                            itemView.top + width,
                            itemView.right - width,
                            itemView.bottom - width
                        )
                        icon?.draw(c)
                        x = max(dX, -maxX)
                    }
                    dX > 0 -> {// Swiping to the right
                        val background = RectF(
                            itemView.left.toFloat(),
                            itemView.top.toFloat(),
                            dX,
                            itemView.bottom.toFloat()
                        )
                        c.drawRect(background, paint)

                        icon?.setBounds(
                            itemView.left + width,
                            itemView.top + width,
                            itemView.left + 2 * width,
                            itemView.bottom - width
                        )
                        icon?.draw(c)
                        x = min(dX, maxX)
                    }
                    else -> {// view is unSwiped
                        val background = RectF(0F, 0F, 0F, 0F)
                        c.drawRect(background, paint)
                    }
                }
            }
            super.onChildDraw(c, recyclerView, viewHolder, x, dY, actionState, isCurrentlyActive)
        }
    }
    //endregion
}