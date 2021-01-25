package es.upsa.mimo.gamercollection.fragments

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.activities.GameDetailActivity
import es.upsa.mimo.gamercollection.adapters.GamesAdapter
import es.upsa.mimo.gamercollection.adapters.OnItemClickListener
import es.upsa.mimo.gamercollection.fragments.base.BaseFragment
import es.upsa.mimo.gamercollection.fragments.popups.OnFiltersSelected
import es.upsa.mimo.gamercollection.fragments.popups.PopupFilterDialogFragment
import es.upsa.mimo.gamercollection.models.FilterModel
import es.upsa.mimo.gamercollection.models.GameResponse
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

        when(item.itemId) {
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
            R.id.action_add -> {

                launchActivity(GameDetailActivity::class.java)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //MARK: - Interface methods

    override fun onItemClick(id: Int) {
        
        val params = mapOf("gameId" to id)
        launchActivityWithExtras(GameDetailActivity::class.java, params)
    }

    override fun onSubItemClick(id: Int) {}

    override fun filter(filters: FilterModel?) {

        viewModel.filters = filters
        menu?.let{
            it.findItem(R.id.action_filter).isVisible = filters == null
            it.findItem(R.id.action_filter_on).isVisible = filters != null
        }
        viewModel.getGames()
    }

    //MARK: - Private methods

    private fun initializeUI() {

        val application = activity?.application
        viewModel = ViewModelProvider(this, GamesViewModelFactory(application)).get(GamesViewModel::class.java)
        setupBindings()

        button_sort.setOnClickListener {
            viewModel.sortGames(requireContext(), resources)
        }

        button_pending.setOnClickListener {

            button_in_progress.isSelected = false
            button_finished.isSelected = false
            buttonClicked(it, Constants.PENDING_STATE)
        }
        button_in_progress.setOnClickListener {

            button_pending.isSelected = false
            button_finished.isSelected = false
            buttonClicked(it, Constants.IN_PROGRESS_STATE)
        }
        button_finished.setOnClickListener {

            button_pending.isSelected = false
            button_in_progress.isSelected = false
            buttonClicked(it, Constants.FINISHED_STATE)
        }

        swipe_refresh_layout.isEnabled = viewModel.swipeRefresh
        swipe_refresh_layout.setColorSchemeResources(R.color.color3)
        swipe_refresh_layout.setProgressBackgroundColorSchemeResource(R.color.color2)
        swipe_refresh_layout.setOnRefreshListener {
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
        var gameNames = ""
        for (game in games) {

            val intent = Intent(requireContext(), GameDetailActivity::class.java).apply {
                putExtra("gameId", game.id)
            }
            val pendingIntent = PendingIntent.getActivity(requireContext(), game.id, intent, PendingIntent.FLAG_ONE_SHOT)

            if(!viewModel.isNotificationLaunched(game.id)) {

                notifications[game.id] =
                    NotificationCompat.Builder(requireContext(), Constants.CHANNEL_ID)
                        .setSmallIcon(R.drawable.app_icon)
                        .setContentTitle(resources.getString(R.string.notification_title, game.name))
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
                    .setBigContentTitle(resources.getString(R.string.summary_notifications_title, games.size))
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

        text_view_games_number.text = resources.getString(R.string.games_number_title, games.size)
        button_pending.text_view_subtitle.text = "$pendingGamesCount"
        button_in_progress.text_view_subtitle.text = "$inProgressGamesCount"
        button_finished.text_view_subtitle.text = "$finishedGamesCount"
    }

    private fun enableStateButtons(enable: Boolean) {

        swipe_refresh_layout.isRefreshing = !enable
        button_pending.isEnabled = enable
        button_in_progress.isEnabled = enable
        button_finished.isEnabled = enable
    }

    private fun buttonClicked(it: View, newState: String) {

        it.isSelected = !it.isSelected
        swipe_refresh_layout.isEnabled = !it.isSelected && viewModel.swipeRefresh
        viewModel.state = if (it.isSelected) newState else null
        viewModel.getGames()
    }
}
