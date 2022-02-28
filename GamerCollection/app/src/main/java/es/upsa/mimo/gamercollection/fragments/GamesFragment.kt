package es.upsa.mimo.gamercollection.fragments

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.activities.GameDetailActivity
import es.upsa.mimo.gamercollection.adapters.GamesAdapter
import es.upsa.mimo.gamercollection.adapters.OnFiltersSelected
import es.upsa.mimo.gamercollection.adapters.OnItemClickListener
import es.upsa.mimo.gamercollection.base.BindingFragment
import es.upsa.mimo.gamercollection.databinding.FragmentGamesBinding
import es.upsa.mimo.gamercollection.extensions.getFormatted
import es.upsa.mimo.gamercollection.extensions.hideSoftKeyboard
import es.upsa.mimo.gamercollection.extensions.toDate
import es.upsa.mimo.gamercollection.extensions.toString
import es.upsa.mimo.gamercollection.fragments.popups.PopupFilterDialogFragment
import es.upsa.mimo.gamercollection.models.FilterModel
import es.upsa.mimo.gamercollection.models.responses.GameResponse
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.utils.Notifications
import es.upsa.mimo.gamercollection.utils.State
import es.upsa.mimo.gamercollection.viewmodelfactories.GamesViewModelFactory
import es.upsa.mimo.gamercollection.viewmodels.GamesViewModel
import kotlinx.android.synthetic.main.state_button.view.*
import java.util.*
import kotlin.math.max

class GamesFragment : BindingFragment<FragmentGamesBinding>(), OnItemClickListener,
    OnFiltersSelected {

    //region Private properties
    private lateinit var viewModel: GamesViewModel
    private lateinit var gamesAdapter: GamesAdapter
    private var menu: Menu? = null
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
        viewModel.fetchGames()
    }

    override fun onStop() {
        super.onStop()
        searchView?.setQuery(Constants.EMPTY_VALUE, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        this.menu = menu
        menu.clear()
        inflater.inflate(R.menu.games_toolbar_menu, menu)
        setupSearchView(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        menu.findItem(R.id.action_filter)?.isVisible = viewModel.filters.value == null
        menu.findItem(R.id.action_filter_on)?.isVisible = viewModel.filters.value != null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_synchronize -> {

                openSyncPopup()
                return true
            }
            R.id.action_filter, R.id.action_filter_on -> {

                filter()
                return true
            }
            R.id.action_sort -> {

                viewModel.sortGames(requireContext(), resources)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    //endregion

    //region Interface methods
    override fun onItemClick(id: Int) {

        val params = mapOf(Constants.GAME_ID to id, Constants.IS_RAWG_GAME to false)
        launchActivityWithExtras(GameDetailActivity::class.java, params)
    }

    override fun onSubItemClick(id: Int) {
    }

    override fun onLoadMoreItemsClick() {
    }

    override fun filter(filters: FilterModel?) {
        viewModel.applyFilters(filters)
    }
    //endregion

    //region Public methods
    fun buttonClicked(it: View) {

        val newState = when (it) {
            binding.buttonPending -> if (it.isSelected) null else State.PENDING_STATE
            binding.buttonInProgress -> if (it.isSelected) null else State.IN_PROGRESS_STATE
            binding.buttonFinished -> if (it.isSelected) null else State.FINISHED_STATE
            else -> null
        }
        viewModel.setState(newState)
    }

    fun goToStartEndList(view: View) {

        when (view) {
            binding.floatingActionButtonStartList -> viewModel.setPosition(ScrollPosition.TOP)
            binding.floatingActionButtonEndList -> viewModel.setPosition(ScrollPosition.END)
        }
    }
    //endregion

    //region Private methods
    private fun initializeUI() {

        val application = activity?.application
        viewModel = ViewModelProvider(
            this,
            GamesViewModelFactory(application)
        )[GamesViewModel::class.java]
        setupBindings()

        with(binding) {

            swipeRefreshLayout.apply {
                isEnabled = this@GamesFragment.viewModel.swipeRefresh
                setColorSchemeResources(R.color.colorPrimary)
                setProgressBackgroundColorSchemeResource(R.color.colorSecondary)
                setOnRefreshListener {
                    this@GamesFragment.viewModel.loadGames()
                    searchView?.setQuery(Constants.EMPTY_VALUE, false)
                }
            }

            gamesAdapter = GamesAdapter(
                this@GamesFragment.viewModel.games.value ?: listOf(),
                this@GamesFragment.viewModel.platforms,
                null,
                this@GamesFragment
            )
            recyclerViewGames.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = gamesAdapter
                addOnScrollListener(object : RecyclerView.OnScrollListener() {

                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        super.onScrollStateChanged(recyclerView, newState)

                        val position =
                            if (!recyclerView.canScrollVertically(-1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                                ScrollPosition.TOP
                            } else if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                                ScrollPosition.END
                            } else {
                                ScrollPosition.MIDDLE
                            }
                        this@GamesFragment.viewModel.setPosition(position)
                    }
                })
                ItemTouchHelper(SwipeController()).attachToRecyclerView(this)
            }

            fragment = this@GamesFragment
            viewModel = this@GamesFragment.viewModel
            lifecycleOwner = this@GamesFragment
        }
    }

    private fun setupBindings() {

        viewModel.gamesLoading.observe(viewLifecycleOwner) { isLoading ->

            enableStateButtons(!isLoading)
            if (isLoading) {
                showLoading()
            } else {
                hideLoading()
            }
        }

        viewModel.gamesError.observe(viewLifecycleOwner) { error ->
            manageError(error)
        }

        viewModel.games.observe(viewLifecycleOwner) {

            val today = Date().toString(
                viewModel.dateFormatToShow,
                viewModel.language
            ).toDate(
                viewModel.dateFormatToShow,
                viewModel.language
            )
            val gamesToNotify = ArrayList<GameResponse>()
            for (game in it) {
                if (game.releaseDate == today)
                    gamesToNotify.add(game)
            }
            if (gamesToNotify.isNotEmpty()) launchNotification(gamesToNotify)
        }

        viewModel.gamesCount.observe(viewLifecycleOwner) {
            setGamesCount(it)
            setTitle(it.size)
        }

        viewModel.gameDeleted.observe(viewLifecycleOwner) { position ->
            position?.let {
                gamesAdapter.notifyItemRemoved(position)
            }
        }

        viewModel.state.observe(viewLifecycleOwner) {

            binding.apply {
                buttonPending.isSelected = it == State.PENDING_STATE
                buttonInProgress.isSelected = it == State.IN_PROGRESS_STATE
                buttonFinished.isSelected = it == State.FINISHED_STATE
            }
        }

        viewModel.filters.observe(viewLifecycleOwner) { filters ->

            menu?.findItem(R.id.action_filter)?.isVisible = filters == null
            menu?.findItem(R.id.action_filter_on)?.isVisible = filters != null
        }

        viewModel.scrollPosition.observe(viewLifecycleOwner) {
            when (it) {

                ScrollPosition.TOP -> binding.recyclerViewGames.scrollToPosition(0)
                ScrollPosition.END -> binding.recyclerViewGames.scrollToPosition(gamesAdapter.itemCount - 1)
                else -> Unit
            }
        }
    }

    private fun filter() {

        val ft: FragmentTransaction = activity?.supportFragmentManager?.beginTransaction() ?: return
        val prev = activity?.supportFragmentManager?.findFragmentByTag("filterPopup")
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)
        val dialogFragment = PopupFilterDialogFragment(viewModel.filters.value, this)
        dialogFragment.isCancelable = false
        dialogFragment.show(ft, "filterPopup")
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun launchNotification(games: List<GameResponse>) {

        val notifications = mutableMapOf<Int, Notification>()
        var gameNames = Constants.EMPTY_VALUE
        for (game in games) {

            val intent = Intent(requireContext(), GameDetailActivity::class.java).apply {
                putExtra(Constants.GAME_ID, game.id)
            }
            val pendingIntent = PendingIntent.getActivity(
                requireContext(),
                game.id,
                intent,
                PendingIntent.FLAG_ONE_SHOT
            )

            if (!viewModel.isNotificationLaunched(game.id)) {

                notifications[game.id] =
                    NotificationCompat.Builder(requireContext(), Notifications.CHANNEL_ID)
                        .setSmallIcon(R.drawable.app_icon)
                        .setContentTitle(
                            resources.getString(
                                R.string.notification_title,
                                game.name
                            )
                        )
                        .setContentText(
                            resources.getString(
                                R.string.notification_description,
                                Date().toString(
                                    viewModel.dateFormatToShow,
                                    viewModel.language
                                ),
                                game.name
                            )
                        )
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setCategory(NotificationCompat.CATEGORY_REMINDER)
                        .setGroup(Notifications.CHANNEL_GROUP)
                        .build()
                gameNames += game.name + ", "
            }
        }
        gameNames = gameNames.dropLast(2)

        val summaryNotification =
            NotificationCompat.Builder(requireContext(), Notifications.CHANNEL_ID)
                .setContentTitle(
                    resources.getQuantityString(
                        R.plurals.summary_notifications_title,
                        games.size,
                        games.size
                    )
                )
                .setContentText(gameNames)
                .setSmallIcon(R.drawable.app_icon)
                .setStyle(
                    NotificationCompat.InboxStyle()
                        .setBigContentTitle(
                            resources.getQuantityString(
                                R.plurals.summary_notifications_title,
                                games.size,
                                games.size
                            )
                        )
                        .setSummaryText(gameNames)
                )
                .setGroup(Notifications.CHANNEL_GROUP)
                .setGroupSummary(true)
                .build()

        with(NotificationManagerCompat.from(requireContext())) {
            for (notification in notifications) {

                notify(notification.key, notification.value)
                viewModel.setNotificationLaunched(notification.key, true)
            }
            if (notifications.isNotEmpty()) notify(0, summaryNotification)
        }
    }

    private fun setGamesCount(games: List<GameResponse>) {

        val filteredGames = games.mapNotNull { it.state }
        val pendingGamesCount = filteredGames.filter { it == State.PENDING_STATE }.size
        val inProgressGamesCount = filteredGames.filter { it == State.IN_PROGRESS_STATE }.size
        val finishedGamesCount = filteredGames.filter { it == State.FINISHED_STATE }.size

        with(binding) {
            buttonPending.text_view_subtitle.text = "$pendingGamesCount"
            buttonInProgress.text_view_subtitle.text = "$inProgressGamesCount"
            buttonFinished.text_view_subtitle.text = "$finishedGamesCount"
        }
    }

    private fun setTitle(gamesCount: Int) {

        val title = resources.getQuantityString(
            R.plurals.games_number_title,
            gamesCount,
            gamesCount.getFormatted()
        )
        (activity as AppCompatActivity?)?.supportActionBar?.title = title
    }

    private fun enableStateButtons(enable: Boolean) {
        with(binding) {

            swipeRefreshLayout.isRefreshing = !enable
            buttonPending.isEnabled = enable
            buttonInProgress.isEnabled = enable
            buttonFinished.isEnabled = enable
        }
    }

    private fun setupSearchView(menu: Menu) {

        val menuItem = menu.findItem(R.id.action_search)
        searchView = menuItem.actionView as SearchView
        searchView?.let { searchView ->

            searchView.queryHint = resources.getString(R.string.search_games)
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

                override fun onQueryTextChange(newText: String): Boolean {

                    viewModel.searchGames(newText)
                    return true
                }

                override fun onQueryTextSubmit(query: String): Boolean {

                    menuItem.collapseActionView()
                    requireActivity().hideSoftKeyboard()
                    return true
                }
            })
        }
        menuItem.setOnActionExpandListener(
            object : MenuItem.OnActionExpandListener {
                override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                    menu.let {
                        it.findItem(R.id.action_synchronize).isVisible = false
                        it.findItem(R.id.action_filter).isVisible = false
                        it.findItem(R.id.action_filter_on).isVisible = false
                        it.findItem(R.id.action_sort).isVisible = false
                    }
                    return true
                }

                override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                    menu.let {
                        it.findItem(R.id.action_synchronize).isVisible = true
                        it.findItem(R.id.action_filter).isVisible = viewModel.filters.value == null
                        it.findItem(R.id.action_filter_on).isVisible =
                            viewModel.filters.value != null
                        it.findItem(R.id.action_sort).isVisible = true
                    }
                    return true
                }

            }
        )
        setupSearchView(Constants.EMPTY_VALUE)
    }
    //endregion

    enum class ScrollPosition {
        TOP, MIDDLE, END
    }

    inner class SwipeController : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

        private val paint = Paint()

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ) = false

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

            val position = viewHolder.adapterPosition
            if (direction == ItemTouchHelper.LEFT) {
                showPopupConfirmationDialog(resources.getString(R.string.game_detail_delete_confirmation),
                    {
                        viewModel.deleteGame(position)
                    },
                    {
                        gamesAdapter.notifyItemChanged(position)
                    })
            }
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

                when {
                    dX < 0 -> {// Swiping to the left
                        paint.color = ContextCompat.getColor(context, R.color.colorPending)
                        val background = RectF(
                            itemView.right.toFloat() + dX,
                            itemView.top.toFloat(),
                            itemView.right.toFloat(),
                            itemView.bottom.toFloat()
                        )
                        c.drawRect(background, paint)

                        val icon =
                            ContextCompat.getDrawable(context, R.drawable.ic_remove_game)
                        icon?.setBounds(
                            itemView.right - 2 * width,
                            itemView.top + width,
                            itemView.right - width,
                            itemView.bottom - width
                        )
                        icon?.draw(c)
                        x = max(dX, -maxX)
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
}
