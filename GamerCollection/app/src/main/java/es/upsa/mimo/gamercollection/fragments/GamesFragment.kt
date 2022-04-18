package es.upsa.mimo.gamercollection.fragments

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.children
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.adapters.GamesAdapter
import es.upsa.mimo.gamercollection.adapters.OnItemClickListener
import es.upsa.mimo.gamercollection.base.BindingFragment
import es.upsa.mimo.gamercollection.databinding.DialogFragmentPopupFilterBinding
import es.upsa.mimo.gamercollection.databinding.FragmentGamesBinding
import es.upsa.mimo.gamercollection.extensions.*
import es.upsa.mimo.gamercollection.models.FilterModel
import es.upsa.mimo.gamercollection.models.responses.GameResponse
import es.upsa.mimo.gamercollection.utils.*
import es.upsa.mimo.gamercollection.viewmodelfactories.GamesViewModelFactory
import es.upsa.mimo.gamercollection.viewmodels.GamesViewModel
import java.util.*

class GamesFragment : BindingFragment<FragmentGamesBinding>(), OnItemClickListener {

    //region Protected properties
    override val statusBarStyle = StatusBarStyle.SECONDARY
    override val hasOptionsMenu = true
    //endregion

    //region Private properties
    private lateinit var viewModel: GamesViewModel
    private lateinit var inProgressGamesAdapter: GamesAdapter
    private lateinit var pendingGamesAdapter: GamesAdapter
    private lateinit var gamesAdapter: GamesAdapter
    private var menu: Menu? = null
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
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        menu.findItem(R.id.action_filter)?.isVisible = viewModel.filters.value == null
        menu.findItem(R.id.action_filter_fill)?.isVisible = viewModel.filters.value != null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_synchronize -> {

                openSyncPopup()
                return true
            }
            R.id.action_filter, R.id.action_filter_fill -> {

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

        val action = GamesFragmentDirections.actionGamesFragmentToGameDetailFragment(id)
        findNavController().navigate(action)
    }

    override fun onSubItemClick(id: Int) {
    }

    override fun onLoadMoreItemsClick() {
    }
    //endregion

    //region Protected methods
    override fun initializeUi() {
        super.initializeUi()

        val application = activity?.application
        viewModel = ViewModelProvider(
            this,
            GamesViewModelFactory(application)
        )[GamesViewModel::class.java]
        setupBindings()

        inProgressGamesAdapter = GamesAdapter(
            viewModel.inProgressGames.value
                ?: listOf(),
            null,
            this
        )
        pendingGamesAdapter = GamesAdapter(
            viewModel.pendingGames.value
                ?: listOf(),
            null,
            this
        )
        gamesAdapter = GamesAdapter(
            viewModel.finishedGames.value ?: listOf(),
            null,
            this
        )

        with(binding) {

            for (recyclerView in listOf(
                recyclerViewInProgressGames,
                recyclerViewPendingGames,
                recyclerViewGames
            )) {
                recyclerView.layoutManager = LinearLayoutManager(
                    requireContext(),
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
            }

            recyclerViewInProgressGames.adapter = inProgressGamesAdapter
            recyclerViewPendingGames.adapter = pendingGamesAdapter
            recyclerViewGames.adapter = gamesAdapter

            fragment = this@GamesFragment
            viewModel = this@GamesFragment.viewModel
            lifecycleOwner = this@GamesFragment
        }
        setupSearchView()
    }
    //endregion

    //region Private methods
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

        viewModel.gameDeleted.observe(viewLifecycleOwner) { position ->
            position?.let {
                gamesAdapter.notifyItemRemoved(position)
            }
        }

        viewModel.filters.observe(viewLifecycleOwner) { filters ->

            menu?.findItem(R.id.action_filter)?.isVisible = filters == null
            menu?.findItem(R.id.action_filter_fill)?.isVisible = filters != null
        }
    }

    private fun filter() {

        val dialogBinding = DialogFragmentPopupFilterBinding.inflate(layoutInflater).apply {

            chipGroupPlatforms.removeAllViews()
            for (platform in Constants.PLATFORMS) {
                chipGroupPlatforms.addChip(layoutInflater, platform.id, platform.name)
            }
            chipGroupGenres.removeAllViews()
            for (genre in Constants.GENRES) {
                chipGroupGenres.addChip(layoutInflater, genre.id, genre.name)
            }
            chipGroupFormats.removeAllViews()
            for (format in Constants.FORMATS) {
                chipGroupFormats.addChip(layoutInflater, format.id, format.name)
            }
            for (view in listOf(
                textInputLayoutReleaseDateMin,
                textInputLayoutReleaseDateMax,
                textInputLayoutPurchaseDateMin,
                textInputLayoutPurchaseDateMax
            )) {
                view.showDatePicker(
                    requireActivity(),
                    SharedPreferencesHelper.filterDateFormat
                )
            }
            filter = viewModel.filters.value
        }
        viewModel.filters.value?.let { filters ->

            val platforms = filters.platforms
            if (platforms.isNotEmpty()) {
                for (child in dialogBinding.chipGroupPlatforms.children) {
                    (child as Chip).isChecked = platforms.firstOrNull { it == child.tag } != null
                }
            }
            val genres = filters.genres
            if (genres.isNotEmpty()) {
                for (child in dialogBinding.chipGroupGenres.children) {
                    (child as Chip).isChecked = genres.firstOrNull { it == child.tag } != null
                }
            }
            val formats = filters.formats
            if (formats.isNotEmpty()) {
                for (child in dialogBinding.chipGroupFormats.children) {
                    (child as Chip).isChecked = formats.firstOrNull { it == child.tag } != null
                }
            }
        }

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogBinding.root)
            .setCancelable(false)
            .setPositiveButton(resources.getString(R.string.accept)) { dialog, _ ->

                val platforms: ArrayList<String> = arrayListOf()
                for (childId in dialogBinding.chipGroupPlatforms.checkedChipIds) {
                    dialogBinding.chipGroupPlatforms.children.find { child ->
                        child.id == childId
                    }?.tag?.let { tag ->
                        platforms.add("$tag")
                    }
                }

                val genres: ArrayList<String> = arrayListOf()
                for (childId in dialogBinding.chipGroupGenres.checkedChipIds) {
                    dialogBinding.chipGroupGenres.children.find { child ->
                        child.id == childId
                    }?.tag?.let { tag ->
                        genres.add("$tag")
                    }
                }

                val formats: ArrayList<String> = arrayListOf()
                for (childId in dialogBinding.chipGroupFormats.checkedChipIds) {
                    dialogBinding.chipGroupFormats.children.find { child ->
                        child.id == childId
                    }?.tag?.let { tag ->
                        formats.add("$tag")
                    }
                }

                val minScore = (dialogBinding.ratingBarMin.rating * 2).toDouble()
                val maxScore = (dialogBinding.ratingBarMax.rating * 2).toDouble()

                val minReleaseDate = dialogBinding.textInputLayoutReleaseDateMin.getValue().toDate(
                    viewModel.filterDateFormat,
                    viewModel.language
                )
                val maxReleaseDate = dialogBinding.textInputLayoutReleaseDateMax.getValue().toDate(
                    viewModel.filterDateFormat,
                    viewModel.language
                )

                val minPurchaseDate =
                    dialogBinding.textInputLayoutPurchaseDateMin.getValue().toDate(
                        viewModel.filterDateFormat,
                        viewModel.language
                    )
                val maxPurchaseDate =
                    dialogBinding.textInputLayoutPurchaseDateMax.getValue().toDate(
                        viewModel.filterDateFormat,
                        viewModel.language
                    )

                var minPrice = 0.0
                try {
                    minPrice = dialogBinding.textInputLayoutPriceMin.getValue().toDouble()
                } catch (e: Exception) {
                }
                var maxPrice = 0.0
                try {
                    maxPrice = dialogBinding.textInputLayoutPriceMax.getValue().toDouble()
                } catch (e: Exception) {
                }

                val isGoty = dialogBinding.radioButtonGotyYes.isChecked
                val isLoaned = dialogBinding.radioButtonLoanedYes.isChecked
                val hasSaga = dialogBinding.radioButtonSagaYes.isChecked
                val hasSongs = dialogBinding.radioButtonSongsYes.isChecked

                val filters = FilterModel(
                    platforms,
                    genres,
                    formats,
                    minScore,
                    maxScore,
                    minReleaseDate,
                    maxReleaseDate,
                    minPurchaseDate,
                    maxPurchaseDate,
                    minPrice,
                    maxPrice,
                    isGoty,
                    isLoaned,
                    hasSaga,
                    hasSongs
                )

                if (
                    platforms.isEmpty() &&
                    genres.isEmpty() &&
                    formats.isEmpty() &&
                    minScore == 0.0 &&
                    maxScore == 10.0 &&
                    minReleaseDate == null &&
                    maxReleaseDate == null &&
                    minPurchaseDate == null &&
                    maxPurchaseDate == null &&
                    minPrice == 0.0 &&
                    maxPrice == 0.0 &&
                    !isGoty &&
                    !isLoaned &&
                    !hasSaga &&
                    !hasSongs
                ) {
                    viewModel.applyFilters(null)
                } else {
                    viewModel.applyFilters(filters)
                }
                dialog.dismiss()
            }
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setNeutralButton(resources.getString(R.string.reset)) { _, _ -> }
            .create()
        dialog.show()

        /*
        This is needed to avoid the auto dismiss
         */
        dialog.getButton(DialogInterface.BUTTON_NEUTRAL).setOnClickListener {
            dialogBinding.apply {

                for (child in chipGroupPlatforms.children) {
                    (child as Chip).isChecked = false
                }
                for (child in chipGroupGenres.children) {
                    (child as Chip).isChecked = false
                }
                for (child in chipGroupFormats.children) {
                    (child as Chip).isChecked = false
                }
                filter = null
            }
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun launchNotification(games: List<GameResponse>) {

        val notifications = mutableMapOf<Int, Notification>()
        var gameNames = Constants.EMPTY_VALUE
        for (game in games) {

            val intent = Intent(requireContext(), GameDetailFragment::class.java).apply {
                putExtra("gameId", game.id)
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

    private fun setupSearchView() {

        this.searchView = binding.searchViewGames
        this.searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {

                viewModel.searchGames(newText)
                binding.apply {
                    recyclerViewGames.scrollToPosition(0)
                }
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {

                requireActivity().hideSoftKeyboard()
                searchView?.clearFocus()
                return true
            }
        })
        this.setupSearchView(R.color.colorSecondary, Constants.EMPTY_VALUE, R.string.search_games)
    }
    //endregion
}
