package es.upsa.mimo.gamercollection.fragments

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.model.LatLng
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.adapters.OnLocationSelected
import es.upsa.mimo.gamercollection.fragments.base.BaseFragment
import es.upsa.mimo.gamercollection.models.responses.GameResponse
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.viewmodelfactories.GameDataViewModelFactory
import es.upsa.mimo.gamercollection.viewmodels.GameDataViewModel
import kotlinx.android.synthetic.main.custom_edit_text.view.*
import kotlinx.android.synthetic.main.fragment_game_data.*

class GameDataFragment(
    private var game: GameResponse? = null
) : BaseFragment(), OnLocationSelected {

    //MARK: - Private properties

    private lateinit var viewModel: GameDataViewModel
    private var genreValues = ArrayList<String>()
    private var formatValues = ArrayList<String>()

    // MARK: - Lifecycle methods

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_game_data, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeUI()
    }

    // MARK: - Public methods

    override fun setLocation(location: LatLng?) {

        var locationText = Constants.EMPTY_VALUE
        location?.let {
            locationText = "${it.latitude},${it.longitude}"
        }
        custom_edit_text_purchase_location.setText(locationText)
    }

    fun showData(game: GameResponse?, enabled: Boolean) {

        val backgroundColor = ContextCompat.getColor(requireContext(), R.color.colorPrimary)

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

        this.game = game
        game?.let {

            game.genre?.let { genreId ->
                val genreName = viewModel.genres.firstOrNull { it.id == genreId }?.name
                val pos = genreValues.indexOf(genreName)
                genrePosition = if(pos > 0) pos else 0
            }

            releaseDate = Constants.dateToString(
                game.releaseDate,
                Constants.getDateFormatToShow(viewModel.language),
                viewModel.language
            ) ?: if (enabled) Constants.EMPTY_VALUE else Constants.NO_VALUE

            game.format?.let { formatId ->
                val formatName = viewModel.formats.firstOrNull { it.id == formatId }?.name
                val pos = formatValues.indexOf(formatName)
                formatPosition = if(pos > 0) pos else 0
            }

            game.state?.let {
                button_pending.isSelected = it == Constants.PENDING_STATE
                button_in_progress.isSelected = it == Constants.IN_PROGRESS_STATE
                button_finished.isSelected = it == Constants.FINISHED_STATE
            }

            distributor = if (game.distributor != null && game.distributor.isNotEmpty()) game.distributor else if (enabled) Constants.EMPTY_VALUE else Constants.NO_VALUE

            developer = if (game.developer != null && game.developer.isNotEmpty()) game.developer else if (enabled) Constants.EMPTY_VALUE else Constants.NO_VALUE

            game.pegi?.let { pegi ->
                val pos = resources.getStringArray(R.array.pegis).indexOf(pegi)
                spinner_pegis.setSelection(pos + 1)
            }

            players = if (game.players != null && game.players.isNotEmpty()) game.players else if (enabled) Constants.EMPTY_VALUE else Constants.NO_VALUE

            custom_edit_text_price.setText(game.price.toString())

            purchaseDate = Constants.dateToString(
                game.purchaseDate,
                Constants.getDateFormatToShow(viewModel.language),
                viewModel.language
            ) ?: if (enabled) Constants.EMPTY_VALUE else Constants.NO_VALUE

            purchaseLocation = if (game.purchaseLocation != null && game.purchaseLocation.isNotEmpty()) game.purchaseLocation else if (enabled) Constants.EMPTY_VALUE else Constants.NO_VALUE

            radio_button_yes.isChecked = game.goty
            radio_button_no.isChecked = !game.goty

            loaned = if (game.loanedTo != null && game.loanedTo.isNotEmpty()) game.loanedTo else if (enabled) Constants.EMPTY_VALUE else Constants.NO_VALUE

            videoUrl = if (game.videoUrl != null && game.videoUrl.isNotEmpty()) game.videoUrl else if (enabled) Constants.EMPTY_VALUE else Constants.NO_VALUE

            observations = if (game.observations != null && game.observations.isNotEmpty()) game.observations else if (enabled) Constants.EMPTY_VALUE else Constants.NO_VALUE

            custom_edit_text_saga.setText(game.saga?.name)
        } ?: run {

            releaseDate = if (enabled) Constants.EMPTY_VALUE else Constants.NO_VALUE
            distributor = if (enabled) Constants.EMPTY_VALUE else Constants.NO_VALUE
            developer = if (enabled) Constants.EMPTY_VALUE else Constants.NO_VALUE
            players = if (enabled) Constants.EMPTY_VALUE else Constants.NO_VALUE
            purchaseDate = if (enabled) Constants.EMPTY_VALUE else Constants.NO_VALUE
            purchaseLocation = if (enabled) Constants.EMPTY_VALUE else Constants.NO_VALUE
            loaned = if (enabled) Constants.EMPTY_VALUE else Constants.NO_VALUE
            videoUrl = if (enabled) Constants.EMPTY_VALUE else Constants.NO_VALUE
            observations = if (enabled) Constants.EMPTY_VALUE else Constants.NO_VALUE
        }

        spinner_genres.setSelection(genrePosition)
        linear_layout_genres.visibility = if (enabled || genrePosition > 0) View.VISIBLE else View.GONE
        custom_edit_text_release_date.setText(releaseDate)
        spinner_formats.setSelection(formatPosition)
        linear_layout_formats.visibility = if (enabled || formatPosition > 0) View.VISIBLE else View.GONE
        custom_edit_text_distributor.setText(distributor)
        custom_edit_text_developer.setText(developer)
        custom_edit_text_players.setText(players)
        custom_edit_text_purchase_date.setText(purchaseDate)
        custom_edit_text_purchase_location.setText(purchaseLocation)
        custom_edit_text_loaned.setText(loaned)
        custom_edit_text_video_url.setText(videoUrl)
        custom_edit_text_observations.setText(observations)

        button_pending.isEnabled = enabled
        button_in_progress.isEnabled = enabled
        button_finished.isEnabled = enabled

        custom_edit_text_release_date.setReadOnly(!enabled, backgroundColor)
        spinner_formats.backgroundTintList = if (!enabled) ColorStateList.valueOf(Color.TRANSPARENT) else ColorStateList.valueOf(backgroundColor)
        spinner_formats.isEnabled = enabled
        spinner_genres.backgroundTintList = if (!enabled) ColorStateList.valueOf(Color.TRANSPARENT) else ColorStateList.valueOf(backgroundColor)
        spinner_genres.isEnabled = enabled

        linear_layout_hidden.visibility = if(enabled) View.VISIBLE else View.GONE

        custom_edit_text_distributor.setReadOnly(!enabled, backgroundColor)
        custom_edit_text_developer.setReadOnly(!enabled, backgroundColor)
        custom_edit_text_players.setReadOnly(!enabled, backgroundColor)
        custom_edit_text_price.setReadOnly(!enabled, backgroundColor)
        custom_edit_text_purchase_date.setReadOnly(!enabled, backgroundColor)
        custom_edit_text_purchase_location.setReadOnly(!enabled, backgroundColor)
        custom_edit_text_loaned.setReadOnly(!enabled, backgroundColor)
        custom_edit_text_video_url.setReadOnly(!enabled, backgroundColor)
        custom_edit_text_observations.setReadOnly(!enabled, backgroundColor)

        custom_edit_text_distributor.edit_text.setOnEditorActionListener { _, _, _ ->
            custom_edit_text_developer.requestFocus()
            true
        }
        custom_edit_text_developer.edit_text.setOnEditorActionListener { _, _, _ ->
            custom_edit_text_players.requestFocus()
            true
        }
        custom_edit_text_players.edit_text.setOnEditorActionListener { _, _, _ ->
            custom_edit_text_price.requestFocus()
            true
        }
        custom_edit_text_price.edit_text.setOnEditorActionListener { _, _, _ ->
            custom_edit_text_loaned.requestFocus()
            true
        }
        custom_edit_text_loaned.edit_text.setOnEditorActionListener { _, _, _ ->
            custom_edit_text_video_url.requestFocus()
            true
        }
        custom_edit_text_video_url.edit_text.setOnEditorActionListener { _, _, _ ->
            custom_edit_text_observations.requestFocus()
            true
        }
    }

    fun getGameData(): GameResponse? {

        val pegi = resources.getStringArray(R.array.pegis).firstOrNull { it == spinner_pegis.selectedItem.toString() }
        val releaseDate = Constants.stringToDate(
            custom_edit_text_release_date.getText(),
            Constants.getDateFormatToShow(viewModel.language),
            viewModel.language
        )
        val format = viewModel.formats.firstOrNull { it.name == spinner_formats.selectedItem.toString() }?.id
        val genre = viewModel.genres.firstOrNull { it.name == spinner_genres.selectedItem.toString() }?.id
        val state = if(button_pending.isSelected) Constants.PENDING_STATE else if (button_in_progress.isSelected) Constants.IN_PROGRESS_STATE else if (button_finished.isSelected) Constants.FINISHED_STATE else null
        val purchaseDate = Constants.stringToDate(
            custom_edit_text_purchase_date.getText(),
            Constants.getDateFormatToShow(viewModel.language),
            viewModel.language
        )
        val price = try { custom_edit_text_price.getText().toDouble() } catch (e: NumberFormatException) { 0.0 }

        return viewModel.getGameData(
            pegi,
            custom_edit_text_distributor.getText(),
            custom_edit_text_developer.getText(),
            custom_edit_text_players.getText(),
            releaseDate,
            radio_button_yes.isChecked,
            format,
            genre,
            state,
            purchaseDate,
            custom_edit_text_purchase_location.getText(),
            price,
            custom_edit_text_video_url.getText(),
            custom_edit_text_loaned.getText(),
            custom_edit_text_observations.getText()
        )
    }

    //MARK: - Private methods

    private fun initializeUI() {

        val application = activity?.application
        viewModel = ViewModelProvider(this, GameDataViewModelFactory(application, game)).get(GameDataViewModel::class.java)
        setupBindings()

        button_pending.setOnClickListener {
            buttonClicked(it)
        }
        button_in_progress.setOnClickListener {
            buttonClicked(it)
        }
        button_finished.setOnClickListener {
            buttonClicked(it)
        }

        formatValues = ArrayList()
        formatValues.run {
            this.add(resources.getString((R.string.game_detail_select_format)))
            this.addAll(viewModel.formats.map { it.name })
        }
        spinner_formats.adapter = Constants.getAdapter(requireContext(), formatValues)
        genreValues = ArrayList()
        genreValues.run {
            this.add(resources.getString((R.string.game_detail_select_genre)))
            this.addAll(viewModel.genres.map { it.name })
        }
        spinner_genres.adapter = Constants.getAdapter(requireContext(), genreValues)

        spinner_pegis.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
        val pegis = ArrayList<String>()
        pegis.run {
            this.add(resources.getString(R.string.game_detail_select_pegi))
            this.addAll(resources.getStringArray(R.array.pegis).toList())
        }
        spinner_pegis.adapter = Constants.getAdapter(requireContext(), pegis)

        custom_edit_text_purchase_location.setOnClickListener { showMap() }
        custom_edit_text_saga.setReadOnly(true, 0)

        showData(game, game == null)
    }

    private fun setupBindings() {

        viewModel.gameDataLoading.observe(viewLifecycleOwner, { isLoading ->

            if (isLoading) {
                showLoading()
            } else {
                hideLoading()
            }
        })

        viewModel.gameDataError.observe(viewLifecycleOwner, { error ->

            if (error == null) {
                activity?.finish()
            } else {
                manageError(error)
            }
        })
    }

    private fun showMap() {

        val ft: FragmentTransaction = activity?.supportFragmentManager?.beginTransaction() ?: return
        val prev = activity?.supportFragmentManager?.findFragmentByTag("mapDialog")
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)

        var location: LatLng? = null
        val purchaseLocation = game?.purchaseLocation
        if (purchaseLocation != null && purchaseLocation.isNotEmpty()) {
            val latLng = purchaseLocation.split(",")
            location = LatLng(latLng[0].toDouble(), latLng[1].toDouble())
        }

        val dialogFragment = MapsFragment(location, this)
        dialogFragment.show(ft, "mapDialog")
    }

    private fun buttonClicked(it: View) {

        button_pending.isSelected = if(it == button_pending) !it.isSelected else false
        button_in_progress.isSelected = if(it == button_in_progress) !it.isSelected else false
        button_finished.isSelected = if(it == button_finished) !it.isSelected else false
    }
}
