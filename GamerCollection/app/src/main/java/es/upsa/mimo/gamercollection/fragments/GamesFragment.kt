package es.upsa.mimo.gamercollection.fragments

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.databinding.ObservableField
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.activities.GameDetailActivity
import es.upsa.mimo.gamercollection.adapters.GamesAdapter
import es.upsa.mimo.gamercollection.adapters.OnFiltersSelected
import es.upsa.mimo.gamercollection.adapters.OnItemClickListener
import es.upsa.mimo.gamercollection.base.BindingFragment
import es.upsa.mimo.gamercollection.databinding.FragmentGamesBinding
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
import kotlin.collections.ArrayList

class GamesFragment : BindingFragment<FragmentGamesBinding>(), OnItemClickListener,
    OnFiltersSelected {

    //region Private properties
    private lateinit var viewModel: GamesViewModel
    private lateinit var gamesAdapter: GamesAdapter
    private var menu: Menu? = null
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
        viewModel.fetchGames()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        this.menu = menu
        menu.clear()
        inflater.inflate(R.menu.games_toolbar_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_synchronize -> {

                openSyncPopup()
                return true
            }
            R.id.action_filter -> {

                filter()
                return true
            }
            R.id.action_filter_on -> {

                filter()
                return true
            }
            R.id.action_sort_on -> {

                viewModel.sortGames(requireContext(), resources)
                return true
            }
            R.id.action_add -> {

                launchActivity(GameDetailActivity::class.java)
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

        viewModel.filters = filters
        menu?.let {
            it.findItem(R.id.action_filter).isVisible = filters == null
            it.findItem(R.id.action_filter_on).isVisible = filters != null
        }
        scrollPosition.set(ScrollPosition.TOP)
        viewModel.fetchGames()
    }
    //endregion

    //region Public methods
    fun buttonClicked(it: View) {

        scrollPosition.set(ScrollPosition.TOP)
        with(binding) {

            swipeRefreshLayout.isEnabled =
                !it.isSelected && this@GamesFragment.viewModel.swipeRefresh
            buttonPending.isSelected = if (it == buttonPending) !it.isSelected else false
            buttonInProgress.isSelected = if (it == buttonInProgress) !it.isSelected else false
            buttonFinished.isSelected = if (it == buttonFinished) !it.isSelected else false
            val newState = when (it) {
                buttonPending -> State.PENDING_STATE
                buttonInProgress -> State.IN_PROGRESS_STATE
                buttonFinished -> State.FINISHED_STATE
                else -> null
            }
            this@GamesFragment.viewModel.state = if (it.isSelected) newState else null
        }
        viewModel.fetchGames()
    }

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
            GamesViewModelFactory(application)
        ).get(GamesViewModel::class.java)
        setupBindings()

        with(binding) {

            swipeRefreshLayout.apply {
                isEnabled = this@GamesFragment.viewModel.swipeRefresh
                setColorSchemeResources(R.color.colorPrimary)
                setProgressBackgroundColorSchemeResource(R.color.colorSecondary)
                setOnRefreshListener {
                    this@GamesFragment.viewModel.loadGames()
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

            fragment = this@GamesFragment
            viewModel = this@GamesFragment.viewModel
            lifecycleOwner = this@GamesFragment
            position = scrollPosition
        }

        scrollPosition.set(ScrollPosition.TOP)
    }

    private fun setupBindings() {

        viewModel.gamesLoading.observe(viewLifecycleOwner, { isLoading ->

            enableStateButtons(!isLoading)
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

            val today = Constants.stringToDate(
                Constants.dateToString(
                    Date(),
                    Constants.getDateFormatToShow(viewModel.language),
                    viewModel.language
                ),
                Constants.getDateFormatToShow(viewModel.language),
                viewModel.language
            )
            val gamesToNotify = ArrayList<GameResponse>()
            for (game in it) {
                if (game.releaseDate == today)
                    gamesToNotify.add(game)
            }
            if (gamesToNotify.isNotEmpty()) launchNotification(gamesToNotify)
        })

        viewModel.gamesCount.observe(viewLifecycleOwner, {
            setGamesCount(it)
            setTitle(it.size)
        })
    }

    private fun filter() {

        val ft: FragmentTransaction = activity?.supportFragmentManager?.beginTransaction() ?: return
        val prev = activity?.supportFragmentManager?.findFragmentByTag("filterPopup")
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)
        val dialogFragment = PopupFilterDialogFragment(viewModel.filters, this)
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
                                Constants.dateToString(
                                    Date(),
                                    Constants.getDateFormatToShow(viewModel.language),
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
            Constants.getFormattedNumber(gamesCount)
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
    //endregion
}

enum class ScrollPosition {
    TOP, MIDDLE, END
}
