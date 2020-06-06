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
import com.google.gson.Gson
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

class GameDetailFragment : BaseFragment(), RatingBar.OnRatingBarChangeListener {

    private var currentGame: GameResponse? = null
    private lateinit var sharedPrefHandler: SharedPreferencesHandler
    private lateinit var formatRepository: FormatRepository
    private lateinit var genreRepository: GenreRepository
    private lateinit var platformRepository: PlatformRepository
    private lateinit var gameRepository: GameRepository
    private lateinit var gameAPIClient: GameAPIClient
    private lateinit var songAPIClient: SongAPIClient
    private lateinit var genres: List<GenreResponse>
    private lateinit var formats: List<FormatResponse>
    private var genreValues = ArrayList<String>()
    private var formatValues = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        currentGame = Gson().fromJson(arguments?.getString("game"), GameResponse::class.java)
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

    override fun onRatingChanged(ratingBar: RatingBar?, rating: Float, fromUser: Boolean) {
//        text_view_rating.text = rating.toString()
    }

    fun enableEdition(enable: Boolean) {

        val inputTypeText = if (enable) InputType.TYPE_CLASS_TEXT else InputType.TYPE_NULL
        val backgroundColor = ContextCompat.getColor(requireContext(), R.color.color2)

        spinner_genres.backgroundTintList = if (!enable) ColorStateList.valueOf(Color.TRANSPARENT) else ColorStateList.valueOf(backgroundColor)
        spinner_genres.isEnabled = enable
        spinner_formats.backgroundTintList = if (!enable) ColorStateList.valueOf(Color.TRANSPARENT) else ColorStateList.valueOf(backgroundColor)
        spinner_formats.isEnabled = enable
        edit_text_release_date.setReadOnly(!enable, InputType.TYPE_NULL, backgroundColor)
        pending_button.isEnabled = enable
        in_progress_button.isEnabled = enable
        finished_button.isEnabled = enable
        edit_text_distributor.setReadOnly(!enable, inputTypeText, backgroundColor)
        edit_text_developer.setReadOnly(!enable, inputTypeText, backgroundColor)
//        spinner_pegis.backgroundTintList = if (!enable) ColorStateList.valueOf(Color.TRANSPARENT) else ColorStateList.valueOf(backgroundColor)
//        spinner_pegis.isEnabled = enable
        edit_text_players.setReadOnly(!enable, inputTypeText, backgroundColor)
        edit_text_price.setReadOnly(!enable, if (enable) InputType.TYPE_CLASS_NUMBER else InputType.TYPE_NULL, backgroundColor)
        edit_text_purchase_date.setReadOnly(!enable, InputType.TYPE_NULL, backgroundColor)
        edit_text_purchase_location.setReadOnly(!enable, inputTypeText, backgroundColor)
        radio_button_no.isClickable = enable
        radio_button_yes.isClickable = enable
        edit_text_loaned.setReadOnly(!enable, inputTypeText, backgroundColor)
        edit_text_video_url.setReadOnly(!enable, if (enable) InputType.TYPE_TEXT_VARIATION_URI else InputType.TYPE_NULL, backgroundColor)
        edit_text_observations.setReadOnly(!enable, inputTypeText, backgroundColor)

        button_delete_game.visibility = if(enable) View.VISIBLE else View.GONE
    }

    fun showData(game: GameResponse?) {

        currentGame = game
        game?.let { game ->

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
//            rating_bar.rating = game.score.toFloat()
            game.state?.let {
                pending_button.isSelected = it == Constants.pending
                in_progress_button.isSelected = it == Constants.inProgress
                finished_button.isSelected = it == Constants.finished
            }
            edit_text_distributor.setText(game.distributor)
            edit_text_developer.setText(game.developer)
//            game.pegi?.let { pegi ->
//                val pos = resources.getStringArray(R.array.pegis).indexOf(pegi)
//                spinner_pegis.setSelection(pos)
//            }
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
        }
    }

    fun getGameData(): GameResponse {

        val id = currentGame?.id ?: 0
//        val score = rating_bar.rating.toDouble()
//        val pegi = resources.getStringArray(R.array.pegis).firstOrNull { it == spinner_pegis.selectedItem.toString() }
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
//        val songs = currentGame?.songs ?: ArrayList()

        return GameResponse(
            id,
            null,
            null,
            0.0,
            null,
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
            null,
            videoUrl,
            loanedTo,
            observations,
            saga,
            ArrayList())
    }

    //MARK: - Private functions

    private fun initializeUI() {

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
        edit_text_release_date.showDatePicker(requireContext())
        edit_text_purchase_date.showDatePicker(requireContext())
        edit_text_saga.setReadOnly(true, InputType.TYPE_NULL, 0)
        button_delete_game.setOnClickListener { deleteGame() }
    }

    private fun loadData() {

        showData(currentGame)
        enableEdition(currentGame == null)
    }

//    private fun showNewSongPopup() {
//
//        val dialogBuilder = AlertDialog.Builder(requireContext()).create()
//        val dialogView = this.layoutInflater.inflate(R.layout.new_song_dialog, null)
//
//        dialogView.button_accept.setOnClickListener {
//
//            val name = dialogView.edit_text_name.text.toString()
//            val singer = dialogView.edit_text_singer.text.toString()
//            val url = dialogView.edit_text_url.text.toString()
//            val song = SongResponse(0, name, singer, url)
//            addSong(song)
//            dialogBuilder.dismiss()
//        }
//
//        dialogBuilder.setView(dialogView)
//        dialogBuilder.show()
//    }

//    private fun addSong(song: SongResponse) {
//
//        currentGame?.let { game ->
//
//            showLoading()
//            songAPIClient.createSong(game.id, song, {
//                updateData()
//            }, {
//                manageError(it)
//            })
//        }
//    }

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

    private fun getAdapter(data: List<String>): SpinnerAdapter {

        val arrayAdapter = SpinnerAdapter(requireContext(), data)
        arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        return arrayAdapter
    }

//    private fun updateData() {
//
//        currentGame?.let { game ->
//            gameAPIClient.getGame(game.id, {
//                gameRepository.updateGame(it)
//
//                currentGame = it
//                showData(currentGame)
//                hideLoading()
//            }, {
//                manageError(it)
//            })
//        }
//    }
}
