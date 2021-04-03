package es.upsa.mimo.gamercollection.fragments

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.activities.GameDetailActivity
import es.upsa.mimo.gamercollection.adapters.GamesAdapter
import es.upsa.mimo.gamercollection.adapters.OnFiltersSelected
import es.upsa.mimo.gamercollection.adapters.OnItemClickListener
import es.upsa.mimo.gamercollection.fragments.base.BaseFragment
import es.upsa.mimo.gamercollection.fragments.popups.PopupFilterDialogFragment
import es.upsa.mimo.gamercollection.models.FilterModel
import es.upsa.mimo.gamercollection.models.responses.GameResponse
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.viewmodelfactories.GamesViewModelFactory
import es.upsa.mimo.gamercollection.viewmodels.GamesViewModel
import kotlinx.android.synthetic.main.fragment_games.*
import kotlinx.android.synthetic.main.state_button.view.*
import java.util.*
import kotlin.collections.ArrayList

class GamesFragment : BaseFragment(), OnItemClickListener, OnFiltersSelected {

    //MARK: - Private properties

    private lateinit var viewModel: GamesViewModel
    private lateinit var gamesAdapter: GamesAdapter
    private var menu: Menu? = null
    private val scrollPosition = MutableLiveData<ScrollPosition>()

    // MARK: - Lifecycle methods

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

    override fun onResume() {
        super.onResume()
        viewModel.getGames()
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

    //MARK: - Interface methods

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
        scrollPosition.value = ScrollPosition.TOP
        viewModel.getGames()
    }

    //MARK: - Private methods

    private fun initializeUI() {

        val application = activity?.application
        viewModel = ViewModelProvider(
            this,
            GamesViewModelFactory(application)
        ).get(GamesViewModel::class.java)
        setupBindings()

        button_pending.setOnClickListener {
            buttonClicked(it, Constants.PENDING_STATE)
        }
        button_in_progress.setOnClickListener {
            buttonClicked(it, Constants.IN_PROGRESS_STATE)
        }
        button_finished.setOnClickListener {
            buttonClicked(it, Constants.FINISHED_STATE)
        }

        swipe_refresh_layout.isEnabled = viewModel.swipeRefresh
        swipe_refresh_layout.setColorSchemeResources(R.color.colorPrimary)
        swipe_refresh_layout.setProgressBackgroundColorSchemeResource(R.color.colorSecondary)
        swipe_refresh_layout.setOnRefreshListener {
            viewModel.loadGames()
        }

        recycler_view_games.layoutManager = LinearLayoutManager(requireContext())
        gamesAdapter = GamesAdapter(
            viewModel.games.value ?: listOf(),
            viewModel.platforms,
            null,
            this
        )
        recycler_view_games.adapter = gamesAdapter
        recycler_view_games.addOnScrollListener(object : RecyclerView.OnScrollListener() {

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

            gamesAdapter.setGames(it)
            layout_empty_list.visibility = if (it.isNotEmpty()) View.GONE else View.VISIBLE
            swipe_refresh_layout.visibility = if (it.isNotEmpty()) View.VISIBLE else View.GONE
            scrollPosition.value =
                if (it.isNotEmpty()) scrollPosition.value else ScrollPosition.NONE

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

        scrollPosition.observe(viewLifecycleOwner, {

            floating_action_button_start_list.visibility =
                if (it == ScrollPosition.TOP || it == ScrollPosition.NONE) View.GONE else View.VISIBLE
            floating_action_button_end_list.visibility =
                if (it == ScrollPosition.END || it == ScrollPosition.NONE) View.GONE else View.VISIBLE
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
                    NotificationCompat.Builder(requireContext(), Constants.CHANNEL_ID)
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
                        .setGroup(Constants.CHANNEL_GROUP)
                        .build()
                gameNames += game.name + ", "
            }
        }
        gameNames = gameNames.dropLast(2)

        val summaryNotification = NotificationCompat.Builder(requireContext(), Constants.CHANNEL_ID)
            .setContentTitle(resources.getString(R.string.summary_notifications_title, games.size))
            .setContentText(gameNames)
            .setSmallIcon(R.drawable.app_icon)
            .setStyle(
                NotificationCompat.InboxStyle()
                    .setBigContentTitle(
                        resources.getString(
                            R.string.summary_notifications_title,
                            games.size
                        )
                    )
                    .setSummaryText(gameNames)
            )
            .setGroup(Constants.CHANNEL_GROUP)
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
        val pendingGamesCount = filteredGames.filter { it == Constants.PENDING_STATE }.size
        val inProgressGamesCount = filteredGames.filter { it == Constants.IN_PROGRESS_STATE }.size
        val finishedGamesCount = filteredGames.filter { it == Constants.FINISHED_STATE }.size

        button_pending.text_view_subtitle.text = "$pendingGamesCount"
        button_in_progress.text_view_subtitle.text = "$inProgressGamesCount"
        button_finished.text_view_subtitle.text = "$finishedGamesCount"
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

        swipe_refresh_layout.isRefreshing = !enable
        button_pending.isEnabled = enable
        button_in_progress.isEnabled = enable
        button_finished.isEnabled = enable
    }

    private fun buttonClicked(it: View, newState: String) {

        button_pending.isSelected = if (it == button_pending) !it.isSelected else false
        button_in_progress.isSelected = if (it == button_in_progress) !it.isSelected else false
        button_finished.isSelected = if (it == button_finished) !it.isSelected else false
        swipe_refresh_layout.isEnabled = !it.isSelected && viewModel.swipeRefresh
        viewModel.state = if (it.isSelected) newState else null
        scrollPosition.value = ScrollPosition.TOP
        viewModel.getGames()
    }
}

enum class ScrollPosition {
    TOP, MIDDLE, END, NONE
}
