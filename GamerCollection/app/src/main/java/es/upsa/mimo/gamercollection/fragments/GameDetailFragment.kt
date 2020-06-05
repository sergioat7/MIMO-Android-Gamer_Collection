package es.upsa.mimo.gamercollection.fragments

import android.app.AlertDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.view.*
import android.widget.RatingBar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.adapters.SongsAdapter
import es.upsa.mimo.gamercollection.adapters.SpinnerAdapter
import es.upsa.mimo.gamercollection.extensions.setReadOnly
import es.upsa.mimo.gamercollection.extensions.showDatePicker
import es.upsa.mimo.gamercollection.fragments.base.BaseFragment
import es.upsa.mimo.gamercollection.models.*
import es.upsa.mimo.gamercollection.network.apiClient.GameAPIClient
import es.upsa.mimo.gamercollection.network.apiClient.SongAPIClient
import es.upsa.mimo.gamercollection.persistence.repositories.FormatRepository
import es.upsa.mimo.gamercollection.persistence.repositories.GameRepository
import es.upsa.mimo.gamercollection.persistence.repositories.GenreRepository
import es.upsa.mimo.gamercollection.persistence.repositories.PlatformRepository
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler
import kotlinx.android.synthetic.main.fragment_game_detail.*
import kotlinx.android.synthetic.main.new_song_dialog.view.*

class GameDetailFragment : BaseFragment(), RatingBar.OnRatingBarChangeListener, SongsAdapter.OnItemClickListener {

    private var gameId: Int? = null
    private lateinit var sharedPrefHandler: SharedPreferencesHandler
    private lateinit var formatRepository: FormatRepository
    private lateinit var genreRepository: GenreRepository
    private lateinit var platformRepository: PlatformRepository
    private lateinit var gameRepository: GameRepository
    private lateinit var gameAPIClient: GameAPIClient
    private lateinit var songAPIClient: SongAPIClient
    private var menu: Menu? = null
    private lateinit var platforms: List<PlatformResponse>
    private lateinit var genres: List<GenreResponse>
    private lateinit var formats: List<FormatResponse>
    private var platformValues = ArrayList<String>()
    private var genreValues = ArrayList<String>()
    private var formatValues = ArrayList<String>()
    private var currentGame: GameResponse? = null
    private var imageUrl: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        gameId = this.arguments?.getInt("gameId")
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_game_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPrefHandler = SharedPreferencesHandler(context)
        formatRepository = FormatRepository(requireContext())
        genreRepository = GenreRepository(requireContext())
        platformRepository = PlatformRepository(requireContext())
        gameRepository = GameRepository(requireContext())
        gameAPIClient = GameAPIClient(resources, sharedPrefHandler)
        songAPIClient = SongAPIClient(resources, sharedPrefHandler)

        initializeUI()
        loadData()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        this.menu = menu
        menu.clear()
        inflater.inflate(R.menu.edit_details_toolbar_menu, menu)
        menu.findItem(R.id.action_edit).isVisible = currentGame != null
        menu.findItem(R.id.action_save).isVisible = currentGame == null
        menu.findItem(R.id.action_cancel).isVisible = false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId) {
            R.id.action_edit -> {
                editGame()
                return true
            }
            R.id.action_save -> {
                saveGame()
                return true
            }
            R.id.action_cancel -> {
                cancelEdition()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRatingChanged(ratingBar: RatingBar?, rating: Float, fromUser: Boolean) {
        text_view_rating.text = rating.toString()
    }

    override fun onItemClick(songId: Int) {

        currentGame?.let { game ->

            showLoading()
            songAPIClient.deleteSong(game.id, songId, {
                updateData()
            }, {
                manageError(it)
            })
        }
    }

    //MARK: - Private functions

    private fun initializeUI() {

        game_image_view.setOnClickListener { setImage() }
        platforms = platformRepository.getPlatforms()
        platformValues = ArrayList<String>()
        platformValues.run {
            this.add(resources.getString((R.string.GAME_DETAIL_SELECT_PLATFORM)))
            this.addAll(platforms.mapNotNull { it.name })
        }
        spinner_platforms.adapter = getAdapter(platformValues)
        genres = genreRepository.getGenres()
        genreValues = ArrayList<String>()
        genreValues.run {
            this.add(resources.getString((R.string.GAME_DETAIL_SELECT_GENRE)))
            this.addAll(genres.mapNotNull { it.name })
        }
        spinner_genres.adapter = getAdapter(genreValues)
        formats = formatRepository.getFormats()
        formatValues = ArrayList<String>()
        formatValues.run {
            this.add(resources.getString((R.string.GAME_DETAIL_SELECT_GENRE)))
            this.addAll(formats.mapNotNull { it.name })
        }
        spinner_formats.adapter = getAdapter(formatValues)
        pending_button.setOnClickListener {
            it.isSelected = !it.isSelected
            in_progress_button.isSelected = false
            finished_button.isSelected = false
        }
        in_progress_button.setOnClickListener {

            pending_button.isSelected = false
            it.isSelected = !it.isSelected
            finished_button.isSelected = false
        }
        finished_button.setOnClickListener {

            pending_button.isSelected = false
            in_progress_button.isSelected = false
            it.isSelected = !it.isSelected
        }
        val pegis = ArrayList<String>()
        pegis.add(resources.getString(R.string.GAME_DETAIL_SELECT_PEGI))
        pegis.addAll(resources.getStringArray(R.array.pegis).toList())
        spinner_pegis.adapter = getAdapter(pegis)
        edit_text_release_date.showDatePicker(requireContext())
        rating_bar.onRatingBarChangeListener = this
        edit_text_purchase_date.showDatePicker(requireContext())
        edit_text_saga.setReadOnly(true, InputType.TYPE_NULL, 0)
        button_add_song.setOnClickListener { showNewSongPopup() }
        recycler_view_songs.layoutManager = LinearLayoutManager(requireContext())
        button_delete_game.setOnClickListener { deleteGame() }
    }

    private fun loadData() {

        gameId?.let {

            showLoading()
            currentGame = gameRepository.getGame(it)
            hideLoading()
        }

        showData(currentGame)
        enableEdition(currentGame == null)
    }

    private fun showData(game: GameResponse?) {

        currentGame = game
        game?.let { game ->

            edit_text_name.setText(game.name)
            imageUrl = game.imageUrl
            imageUrl?.let { url ->
                Picasso.with(requireContext()).load(url).error(R.drawable.add_photo).into(game_image_view)
            }
            goty_image_view.visibility = if(game.goty) View.VISIBLE else View.GONE
            game.platform?.let { platformId ->
                val platformName = platformRepository.getPlatforms().firstOrNull { it.id == platformId }?.name
                val pos = platformValues.indexOf(platformName)
                spinner_platforms.setSelection( if(pos > 0) pos else 0 )
            }
            game.genre?.let { genreId ->
                val genreName = genreRepository.getGenres().firstOrNull { it.id == genreId }?.name
                val pos = genreValues.indexOf(genreName)
                spinner_genres.setSelection( if(pos > 0) pos else 0 )
            }
            game.format?.let { formatId ->
                val formatName = formatRepository.getFormats().firstOrNull { it.id == formatId }?.name
                val pos = formatValues.indexOf(formatName)
                spinner_formats.setSelection( if(pos > 0) pos else 0 )
            }
            edit_text_release_date.setText(Constants.dateToString(game.releaseDate, sharedPrefHandler))
            rating_bar.rating = game.score.toFloat()
            game.state?.let {
                pending_button.isSelected = it == Constants.pending
                in_progress_button.isSelected = it == Constants.inProgress
                finished_button.isSelected = it == Constants.finished
            }
            edit_text_distributor.setText(game.distributor)
            edit_text_developer.setText(game.developer)
            game.pegi?.let { pegi ->
                val pos = resources.getStringArray(R.array.pegis).indexOf(pegi)
                spinner_pegis.setSelection(pos)
            }
            edit_text_players.setText(game.players)
            edit_text_price.setText(game.price.toString())
            edit_text_purchase_date.setText(Constants.dateToString(game.purchaseDate, sharedPrefHandler))
            edit_text_purchase_location.setText(game.purchaseLocation)
            radio_button_yes.isChecked = game.goty
            radio_button_no.isChecked = !game.goty
            edit_text_loaned.setText(game.loanedTo)
            edit_text_video_url.setText(game.videoUrl)
            edit_text_observations.setText(game.observations)
            edit_text_saga.setText(game.saga?.name)
            var editable = false
            menu?.let {
                editable = it.findItem(R.id.action_save).isVisible
            }
            recycler_view_songs.adapter = SongsAdapter(game.songs, editable, this)
        }
    }

    private fun enableEdition(enable: Boolean) {

        val inputTypeText = if (enable) InputType.TYPE_CLASS_TEXT else InputType.TYPE_NULL
        val backgroundColor = ContextCompat.getColor(requireContext(), R.color.color2)

        edit_text_name.setReadOnly(!enable, inputTypeText, backgroundColor)
        game_image_view.isEnabled = enable
        spinner_platforms.backgroundTintList = if (!enable) ColorStateList.valueOf(Color.TRANSPARENT) else ColorStateList.valueOf(backgroundColor)
        spinner_platforms.isEnabled = enable
        spinner_genres.backgroundTintList = if (!enable) ColorStateList.valueOf(Color.TRANSPARENT) else ColorStateList.valueOf(backgroundColor)
        spinner_genres.isEnabled = enable
        spinner_formats.backgroundTintList = if (!enable) ColorStateList.valueOf(Color.TRANSPARENT) else ColorStateList.valueOf(backgroundColor)
        spinner_formats.isEnabled = enable
        edit_text_release_date.setReadOnly(!enable, InputType.TYPE_NULL, backgroundColor)
        rating_bar.setIsIndicator(!enable)
        pending_button.isEnabled = enable
        in_progress_button.isEnabled = enable
        finished_button.isEnabled = enable
        edit_text_distributor.setReadOnly(!enable, inputTypeText, backgroundColor)
        edit_text_developer.setReadOnly(!enable, inputTypeText, backgroundColor)
        spinner_pegis.backgroundTintList = if (!enable) ColorStateList.valueOf(Color.TRANSPARENT) else ColorStateList.valueOf(backgroundColor)
        spinner_pegis.isEnabled = enable
        edit_text_players.setReadOnly(!enable, inputTypeText, backgroundColor)
        edit_text_price.setReadOnly(!enable, if (enable) InputType.TYPE_CLASS_NUMBER else InputType.TYPE_NULL, backgroundColor)
        edit_text_purchase_date.setReadOnly(!enable, InputType.TYPE_NULL, backgroundColor)
        edit_text_purchase_location.setReadOnly(!enable, inputTypeText, backgroundColor)
        radio_button_no.isClickable = enable
        radio_button_yes.isClickable = enable
        edit_text_loaned.setReadOnly(!enable, inputTypeText, backgroundColor)
        edit_text_video_url.setReadOnly(!enable, if (enable) InputType.TYPE_TEXT_VARIATION_URI else InputType.TYPE_NULL, backgroundColor)
        edit_text_observations.setReadOnly(!enable, inputTypeText, backgroundColor)
        button_add_song.visibility = if (enable && currentGame != null) View.VISIBLE else View.GONE
        currentGame?.let {
            val adapter = recycler_view_songs.adapter as? SongsAdapter
            if (adapter != null) {
                adapter.editable = enable
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun getGameData(): GameResponse? {

        val id = currentGame?.id ?: 0
        val name = edit_text_name.text.toString()
        val platform = platforms.firstOrNull { it.name == spinner_platforms.selectedItem.toString() }?.id
        val score = rating_bar.rating.toDouble()
        val pegi = resources.getStringArray(R.array.pegis).firstOrNull { it == spinner_pegis.selectedItem.toString() }
        val distributor = edit_text_distributor.text.toString()
        val developer = edit_text_developer.text.toString()
        val players = edit_text_players.text.toString()
        val releaseDate = Constants.stringToDate(edit_text_release_date.text.toString(), sharedPrefHandler)
        val goty = radio_button_yes.isChecked
        val format = formats.firstOrNull { it.name == spinner_formats.selectedItem.toString() }?.id
        val genre = genres.firstOrNull { it.name == spinner_genres.selectedItem.toString() }?.id
        val state = if(pending_button.isSelected) Constants.pending else if (in_progress_button.isSelected) Constants.inProgress else if (finished_button.isSelected) Constants.finished else null
        val purchaseDate = Constants.stringToDate(edit_text_purchase_date.text.toString(), sharedPrefHandler)
        val purchaseLocation = edit_text_purchase_location.text.toString()
        val price = try { edit_text_price.text.toString().toDouble() } catch (e: NumberFormatException) { 0.0 }
        val videoUrl = edit_text_video_url.text.toString()
        val loanedTo = edit_text_loaned.text.toString()
        val observations = edit_text_observations.text.toString()
        val saga = currentGame?.saga
        val songs = currentGame?.songs ?: ArrayList()

        if (name.isEmpty() &&
            platform == null &&
            score == 0.0 &&
            pegi == null &&
            distributor.isEmpty() &&
            developer.isEmpty() &&
            players.isEmpty() &&
            releaseDate == null &&
            !goty &&
            format == null &&
            genre == null &&
            state == null &&
            purchaseDate == null &&
            purchaseLocation.isEmpty() &&
            price == 0.0 &&
            videoUrl.isEmpty() &&
            loanedTo.isEmpty() &&
            observations.isEmpty() &&
            saga == null &&
            songs.isEmpty()) {
            return null
        } else {
            return GameResponse(
                id,
                name,
                platform,
                score,
                pegi,
                distributor,
                developer,
                players,
                releaseDate,
                goty,
                format,
                genre,
                state,
                purchaseDate,
                purchaseLocation,
                price,
                imageUrl,
                videoUrl,
                loanedTo,
                observations,
                saga,
                songs)
        }
    }

    private fun setImage() {

        val dialogBuilder = AlertDialog.Builder(requireContext()).create()
        val dialogView = this.layoutInflater.inflate(R.layout.set_image_dialog, null)

        dialogView.button_accept.setOnClickListener {

            val url = dialogView.edit_text_url.text.toString()
            if (!url.isEmpty()) {

                Picasso.with(requireContext())
                    .load(url)
                    .error(R.drawable.add_photo)
                    .into(game_image_view, object : Callback {
                        override fun onSuccess() {
                            imageUrl = url
                        }
                        override fun onError() {
                            imageUrl = null
                            showPopupDialog(resources.getString(R.string.ERROR_IMAGE_URL))
                        }
                    })
            }
            dialogBuilder.dismiss()
        }

        dialogBuilder.setView(dialogView)
        dialogBuilder.show()
    }

    private fun showNewSongPopup() {

        val dialogBuilder = AlertDialog.Builder(requireContext()).create()
        val dialogView = this.layoutInflater.inflate(R.layout.new_song_dialog, null)

        dialogView.button_accept.setOnClickListener {

            val name = dialogView.edit_text_name.text.toString()
            val singer = dialogView.edit_text_singer.text.toString()
            val url = dialogView.edit_text_url.text.toString()
            val song = SongResponse(0, name, singer, url)
            addSong(song)
            dialogBuilder.dismiss()
        }

        dialogBuilder.setView(dialogView)
        dialogBuilder.show()
    }

    private fun addSong(song: SongResponse) {

        currentGame?.let { game ->

            showLoading()
            songAPIClient.createSong(game.id, song, {
                updateData()
            }, {
                manageError(it)
            })
        }
    }

    private fun deleteGame() {

        currentGame?.let {
            showPopupConfirmationDialog(resources.getString(R.string.GAME_DETAIL_DELETE_CONFIRMATION)) {

                showLoading()
                gameAPIClient.deleteGame(it.id, {
                    gameRepository.deleteGame(it)

                    hideLoading()
                    activity?.finish()
                }, {
                    manageError(it)
                })
            }
        }
    }

    private fun editGame(){

        showEditButton(true)
        enableEdition(true)
    }

    private fun saveGame() {

        getGameData()?.let { game ->
            showLoading()
            if (currentGame != null) {

                gameAPIClient.setGame(game, {
                    gameRepository.updateGame(it)

                    currentGame = it
                    cancelEdition()
                    hideLoading()
                }, {
                    manageError(it)
                })
            } else {

                gameAPIClient.createGame(game, {
                    gameAPIClient.getGames({ games ->

                        for (game in games) {
                            gameRepository.insertGame(game)
                        }
                        hideLoading()
                        activity?.finish()
                    }, {
                        manageError(it)
                    })
                }, {
                    manageError(it)
                })
            }
        }
    }

    private fun cancelEdition(){

        showEditButton(false)
        showData(currentGame)
        enableEdition(false)
    }

    private fun showEditButton(hidden: Boolean) {

        menu?.let {
            it.findItem(R.id.action_edit).isVisible = !hidden
            it.findItem(R.id.action_save).isVisible = hidden
            it.findItem(R.id.action_cancel).isVisible = hidden
        }
    }

    private fun getAdapter(data: List<String>): SpinnerAdapter {

        val arrayAdapter = SpinnerAdapter(requireContext(), data)
        arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        return arrayAdapter
    }

    private fun updateData() {

        currentGame?.let { game ->
            gameAPIClient.getGame(game.id, {
                gameRepository.updateGame(it)

                currentGame = it
                showData(currentGame)
                hideLoading()
            }, {
                manageError(it)
            })
        }
    }
}
