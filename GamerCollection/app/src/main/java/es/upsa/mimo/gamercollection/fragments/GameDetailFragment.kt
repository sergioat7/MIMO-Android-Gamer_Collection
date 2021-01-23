package es.upsa.mimo.gamercollection.fragments

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.maps.model.LatLng
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.extensions.setReadOnly
import es.upsa.mimo.gamercollection.extensions.showDatePicker
import es.upsa.mimo.gamercollection.fragments.base.BaseFragment
import es.upsa.mimo.gamercollection.injection.GamerCollectionApplication
import es.upsa.mimo.gamercollection.models.FormatResponse
import es.upsa.mimo.gamercollection.models.GameResponse
import es.upsa.mimo.gamercollection.models.GenreResponse
import es.upsa.mimo.gamercollection.network.apiClient.GameAPIClient
import es.upsa.mimo.gamercollection.network.apiClient.SongAPIClient
import es.upsa.mimo.gamercollection.repositories.FormatRepository
import es.upsa.mimo.gamercollection.repositories.GameRepository
import es.upsa.mimo.gamercollection.repositories.GenreRepository
import es.upsa.mimo.gamercollection.repositories.PlatformRepository
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler
import kotlinx.android.synthetic.main.fragment_game_detail.*
import javax.inject.Inject

class GameDetailFragment(
    private var currentGame: GameResponse? = null
) : BaseFragment(), OnLocationSelected {

    @Inject
    lateinit var sharedPrefHandler: SharedPreferencesHandler
    @Inject
    lateinit var formatRepository: FormatRepository
    @Inject
    lateinit var gameRepository: GameRepository
    @Inject
    lateinit var genreRepository: GenreRepository
    @Inject
    lateinit var platformRepository: PlatformRepository
    @Inject
    lateinit var gameAPIClient: GameAPIClient
    private lateinit var genres: List<GenreResponse>
    private lateinit var formats: List<FormatResponse>
    private var genreValues = ArrayList<String>()
    private var formatValues = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_game_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val application = activity?.application
        (application as GamerCollectionApplication).appComponent.inject(this)

        initializeUI()
        loadData()
    }

    override fun setLocation(location: LatLng?) {

        var locationText = ""
        location?.let {
            locationText = "${it.latitude},${it.longitude}"
        }
        edit_text_purchase_location.setText(locationText)
    }

    fun showData(game: GameResponse?, enabled: Boolean) {

        val inputTypeText = if (enabled) InputType.TYPE_CLASS_TEXT else InputType.TYPE_NULL
        val backgroundColor = ContextCompat.getColor(requireContext(), R.color.color2)

        var genrePosition = 0
        var releaseDate: String? = null
        var formatPosition = 0
        var distributor: String? = null
        var developer: String? = null
        var players: String? = null
        var purchaseDate: String? = null
        var purchaseLocation: String? = null
        var loaned: String? = null
        var videoUrl: String? = null
        var observations: String? = null

        currentGame = game
        game?.let {

            game.genre?.let { genreId ->
                val genreName = genreRepository.getGenres().firstOrNull { it.id == genreId }?.name
                val pos = genreValues.indexOf(genreName)
                genrePosition = if(pos > 0) pos else 0
            }

            releaseDate = Constants.dateToString(
                game.releaseDate,
                Constants.getDateFormatToShow(sharedPrefHandler),
                sharedPrefHandler.getLanguage()
            ) ?: if (enabled) "" else "-"

            game.format?.let { formatId ->
                val formatName = formatRepository.getFormats().firstOrNull { it.id == formatId }?.name
                val pos = formatValues.indexOf(formatName)
                formatPosition = if(pos > 0) pos else 0
            }

            game.state?.let {
                button_pending.isSelected = it == Constants.PENDING_STATE
                button_in_progress.isSelected = it == Constants.IN_PROGRESS_STATE
                button_finished.isSelected = it == Constants.FINISHED_STATE
            }

            distributor = if (game.distributor != null && game.distributor.isNotEmpty()) game.distributor else if (enabled) "" else "-"

            developer = if (game.developer != null && game.developer.isNotEmpty()) game.developer else if (enabled) "" else "-"

            game.pegi?.let { pegi ->
                val pos = resources.getStringArray(R.array.pegis).indexOf(pegi)
                spinner_pegis.setSelection(pos+1)
            }

            players = if (game.players != null && game.players.isNotEmpty()) game.players else if (enabled) "" else "-"

            edit_text_price.setText(game.price.toString())

            purchaseDate = Constants.dateToString(
                game.purchaseDate,
                Constants.getDateFormatToShow(sharedPrefHandler),
                sharedPrefHandler.getLanguage()
            ) ?: if (enabled) "" else "-"

            purchaseLocation = if (game.purchaseLocation != null && game.purchaseLocation.isNotEmpty()) game.purchaseLocation else if (enabled) "" else "-"

            radio_button_yes.isChecked = game.goty
            radio_button_no.isChecked = !game.goty

            loaned = if (game.loanedTo != null && game.loanedTo.isNotEmpty()) game.loanedTo else if (enabled) "" else "-"

            videoUrl = if (game.videoUrl != null && game.videoUrl.isNotEmpty()) game.videoUrl else if (enabled) "" else "-"

            observations = if (game.observations != null && game.observations.isNotEmpty()) game.observations else if (enabled) "" else "-"

            edit_text_saga.setText(game.saga?.name)
        } ?: run {

            releaseDate = if (enabled) "" else "-"
            distributor = if (enabled) "" else "-"
            developer = if (enabled) "" else "-"
            players = if (enabled) "" else "-"
            purchaseDate = if (enabled) "" else "-"
            purchaseLocation = if (enabled) "" else "-"
            loaned = if (enabled) "" else "-"
            videoUrl = if (enabled) "" else "-"
            observations = if (enabled) "" else "-"
        }

        spinner_genres.setSelection(genrePosition)
        linear_layout_genres.visibility = if (enabled || genrePosition > 0) View.VISIBLE else View.GONE
        edit_text_release_date.setText(releaseDate)
        spinner_formats.setSelection(formatPosition)
        linear_layout_formats.visibility = if (enabled || formatPosition > 0) View.VISIBLE else View.GONE
        edit_text_distributor.setText(distributor)
        edit_text_developer.setText(developer)
        edit_text_players.setText(players)
        edit_text_purchase_date.setText(purchaseDate)
        edit_text_purchase_location.setText(purchaseLocation)
        edit_text_loaned.setText(loaned)
        edit_text_video_url.setText(videoUrl)
        edit_text_observations.setText(observations)

        button_pending.isEnabled = enabled
        button_in_progress.isEnabled = enabled
        button_finished.isEnabled = enabled

        edit_text_release_date.setReadOnly(!enabled, InputType.TYPE_NULL, backgroundColor)
        spinner_formats.backgroundTintList = if (!enabled) ColorStateList.valueOf(Color.TRANSPARENT) else ColorStateList.valueOf(backgroundColor)
        spinner_formats.isEnabled = enabled
        spinner_genres.backgroundTintList = if (!enabled) ColorStateList.valueOf(Color.TRANSPARENT) else ColorStateList.valueOf(backgroundColor)
        spinner_genres.isEnabled = enabled

        linear_layout_hidden.visibility = if(enabled) View.VISIBLE else View.GONE

        edit_text_distributor.setReadOnly(!enabled, inputTypeText, backgroundColor)
        edit_text_developer.setReadOnly(!enabled, inputTypeText, backgroundColor)
        edit_text_players.setReadOnly(!enabled, inputTypeText, backgroundColor)
        edit_text_price.setReadOnly(!enabled, if (enabled) InputType.TYPE_CLASS_NUMBER else InputType.TYPE_NULL, backgroundColor)
        edit_text_purchase_date.setReadOnly(!enabled, InputType.TYPE_NULL, backgroundColor)
        edit_text_purchase_location.setReadOnly(!enabled, InputType.TYPE_NULL, backgroundColor)
        edit_text_loaned.setReadOnly(!enabled, inputTypeText, backgroundColor)
        edit_text_video_url.setReadOnly(!enabled, if (enabled) InputType.TYPE_TEXT_VARIATION_URI else InputType.TYPE_NULL, backgroundColor)
        edit_text_observations.setReadOnly(!enabled, inputTypeText, backgroundColor)

        button_delete_game.visibility = if(enabled && currentGame != null) View.VISIBLE else View.GONE
    }

    fun getGameData(): GameResponse? {

        val id = currentGame?.id ?: 0
        val pegi = resources.getStringArray(R.array.pegis).firstOrNull { it == spinner_pegis.selectedItem.toString() }
        val distributor = edit_text_distributor.text.toString()
        val developer = edit_text_developer.text.toString()
        val players = edit_text_players.text.toString()
        val releaseDate = Constants.stringToDate(
            edit_text_release_date.text.toString(),
            Constants.getDateFormatToShow(sharedPrefHandler),
            sharedPrefHandler.getLanguage()
        )
        val goty = radio_button_yes.isChecked
        val format = formats.firstOrNull { it.name == spinner_formats.selectedItem.toString() }?.id
        val genre = genres.firstOrNull { it.name == spinner_genres.selectedItem.toString() }?.id
        val state = if(button_pending.isSelected) Constants.PENDING_STATE else if (button_in_progress.isSelected) Constants.IN_PROGRESS_STATE else if (button_finished.isSelected) Constants.FINISHED_STATE else null
        val purchaseDate = Constants.stringToDate(
            edit_text_purchase_date.text.toString(),
            Constants.getDateFormatToShow(sharedPrefHandler),
            sharedPrefHandler.getLanguage()
        )
        val purchaseLocation = edit_text_purchase_location.text.toString()
        val price = try { edit_text_price.text.toString().toDouble() } catch (e: NumberFormatException) { 0.0 }
        val videoUrl = edit_text_video_url.text.toString()
        val loanedTo = edit_text_loaned.text.toString()
        val observations = edit_text_observations.text.toString()
        val saga = currentGame?.saga

        if (pegi == null &&
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
            saga == null) {
            return null
        } else {
            return GameResponse(
                id,
                null,
                null,
                0.0,
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
                null,
                videoUrl,
                loanedTo,
                observations,
                saga,
                ArrayList())
        }
    }

    //MARK: - Private functions

    private fun initializeUI() {

        button_pending.setOnClickListener {
            it.isSelected = !it.isSelected
            button_in_progress.isSelected = false
            button_finished.isSelected = false
        }
        button_in_progress.setOnClickListener {

            button_pending.isSelected = false
            it.isSelected = !it.isSelected
            button_finished.isSelected = false
        }
        button_finished.setOnClickListener {

            button_pending.isSelected = false
            button_in_progress.isSelected = false
            it.isSelected = !it.isSelected
        }

        edit_text_release_date.showDatePicker(requireContext())
        formats = formatRepository.getFormats()
        formatValues = ArrayList()
        formatValues.run {
            this.add(resources.getString((R.string.game_detail_select_format)))
            this.addAll(formats.map { it.name })
        }
        spinner_formats.adapter = Constants.getAdapter(requireContext(), formatValues)
        genres = genreRepository.getGenres()
        genreValues = ArrayList()
        genreValues.run {
            this.add(resources.getString((R.string.game_detail_select_genre)))
            this.addAll(genres.map { it.name })
        }
        spinner_genres.adapter = Constants.getAdapter(requireContext(), genreValues)

        spinner_pegis.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.color2))
        val pegis = ArrayList<String>()
        pegis.add(resources.getString(R.string.game_detail_select_pegi))
        pegis.addAll(resources.getStringArray(R.array.pegis).toList())
        spinner_pegis.adapter = Constants.getAdapter(requireContext(), pegis)
        edit_text_purchase_date.showDatePicker(requireContext())
        edit_text_purchase_location.setOnClickListener { showMap() }
        edit_text_saga.setReadOnly(true, InputType.TYPE_NULL, 0)
        button_delete_game.setOnClickListener { deleteGame() }
    }

    private fun loadData() {
        showData(currentGame, currentGame == null)
    }

    private fun showMap() {

        val ft: FragmentTransaction = activity?.supportFragmentManager?.beginTransaction() ?: return
        val prev = activity?.supportFragmentManager?.findFragmentByTag("mapDialog")
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)

        var location: LatLng? = null
        val purchaseLocation = currentGame?.purchaseLocation
        if (purchaseLocation != null && purchaseLocation.isNotEmpty()) {
            val latLng = purchaseLocation.split(",")
            location = LatLng(latLng[0].toDouble(), latLng[1].toDouble())
        }

        val dialogFragment = MapsFragment(location, this)
        dialogFragment.show(ft, "mapDialog")
    }

    private fun deleteGame() {

        currentGame?.let {
            showPopupConfirmationDialog(resources.getString(R.string.game_detail_delete_confirmation)) {

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
}
