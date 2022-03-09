package es.upsa.mimo.gamercollection.fragments

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.model.LatLng
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.adapters.OnLocationSelected
import es.upsa.mimo.gamercollection.base.BindingFragment
import es.upsa.mimo.gamercollection.databinding.FragmentGameDataBinding
import es.upsa.mimo.gamercollection.extensions.toDate
import es.upsa.mimo.gamercollection.extensions.toString
import es.upsa.mimo.gamercollection.models.responses.GameResponse
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.utils.State
import es.upsa.mimo.gamercollection.utils.StatusBarStyle
import es.upsa.mimo.gamercollection.viewmodelfactories.GameDataViewModelFactory
import es.upsa.mimo.gamercollection.viewmodels.GameDataViewModel
import kotlinx.android.synthetic.main.custom_edit_text.view.*

class GameDataFragment(
    private var game: GameResponse? = null,
    private var enabled: Boolean
) : BindingFragment<FragmentGameDataBinding>(), OnLocationSelected {

    //region Protected properties
    override val statusBarStyle = StatusBarStyle.SECONDARY
    //endregion

    //region Private properties
    private lateinit var viewModel: GameDataViewModel
    private var genreValues = ArrayList<String>()
    private var formatValues = ArrayList<String>()
    //endregion

    //region Lifecycle methods
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeUI()
    }
    //endregion

    //region Public methods
    override fun setLocation(location: LatLng?) {

        var locationText = Constants.EMPTY_VALUE
        location?.let {
            locationText = "${it.latitude},${it.longitude}"
        }
        binding.customEditTextPurchaseLocation.setText(locationText)
    }

    fun showData(game: GameResponse?) {

        var genrePosition = 0
        game?.genre?.let { genreId ->
            val genreName = viewModel.genres.firstOrNull { it.id == genreId }?.name
            val pos = genreValues.indexOf(genreName)
            genrePosition = if (pos > 0) pos else 0
        }
        binding.spinnerGenres.setSelection(genrePosition)

        var formatPosition = 0
        game?.format?.let { formatId ->
            val formatName = viewModel.formats.firstOrNull { it.id == formatId }?.name
            val pos = formatValues.indexOf(formatName)
            formatPosition = if (pos > 0) pos else 0
        }
        binding.spinnerFormats.setSelection(formatPosition)

        game?.state?.let {
            binding.buttonPending.root.isSelected = it == State.PENDING_STATE
            binding.buttonInProgress.root.isSelected = it == State.IN_PROGRESS_STATE
            binding.buttonFinished.root.isSelected = it == State.FINISHED_STATE
        }

        val releaseDate = getText(
            game?.releaseDate.toString(
                viewModel.dateFormatToShow,
                viewModel.language
            )
        )
        binding.customEditTextReleaseDate.setText(releaseDate)
        binding.customEditTextReleaseDate.setDatePickerFormat(requireActivity())


        val distributor = getText(game?.distributor)
        binding.customEditTextDistributor.setText(distributor)

        val developer = getText(game?.developer)
        binding.customEditTextDeveloper.setText(developer)

        game?.pegi?.let { pegi ->
            val pos = resources.getStringArray(R.array.pegis).indexOf(pegi)
            binding.spinnerPegis.setSelection(pos + 1)
        }

        val players = getText(game?.players)
        binding.customEditTextPlayers.setText(players)

        val price = game?.price ?: 0
        binding.customEditTextPrice.setText(price.toString())

        val purchaseDate = getText(
            game?.purchaseDate.toString(
                viewModel.dateFormatToShow,
                viewModel.language
            )
        )
        binding.customEditTextPurchaseDate.setText(purchaseDate)
        binding.customEditTextPurchaseDate.setDatePickerFormat(requireActivity())

        val purchaseLocation = getText(game?.purchaseLocation)
        binding.customEditTextPurchaseLocation.setText(purchaseLocation)

        val loaned = getText(game?.loanedTo)
        binding.customEditTextLoaned.setText(loaned)

        val videoUrl = getText(game?.videoUrl)
        binding.customEditTextVideoUrl.setText(videoUrl)

        val observations = getText(game?.observations)
        binding.customEditTextObservations.setText(observations)

        binding.customEditTextSaga.setText(game?.saga?.name)

        binding.game = game
    }

    fun setEdition(editable: Boolean) {

        enabled = editable

        val backgroundColor = ContextCompat.getColor(requireContext(), R.color.colorPrimary)

        binding.linearLayoutFormats.visibility =
            if (editable || binding.spinnerFormats.selectedItemPosition > 0) View.VISIBLE
            else View.GONE

        binding.linearLayoutGenres.visibility =
            if (editable || binding.spinnerGenres.selectedItemPosition > 0) View.VISIBLE
            else View.GONE

        binding.buttonPending.root.isEnabled = editable
        binding.buttonInProgress.root.isEnabled = editable
        binding.buttonFinished.root.isEnabled = editable

        binding.spinnerFormats.backgroundTintList =
            if (!editable) ColorStateList.valueOf(Color.TRANSPARENT)
            else ColorStateList.valueOf(backgroundColor)
        binding.spinnerFormats.isEnabled = editable
        binding.spinnerGenres.backgroundTintList =
            if (!editable) ColorStateList.valueOf(Color.TRANSPARENT)
            else ColorStateList.valueOf(backgroundColor)
        binding.spinnerGenres.isEnabled = editable

        binding.customEditTextReleaseDate.setReadOnly(!editable, backgroundColor)
        binding.customEditTextDistributor.setReadOnly(!editable, backgroundColor)
        binding.customEditTextDeveloper.setReadOnly(!editable, backgroundColor)
        binding.customEditTextPlayers.setReadOnly(!editable, backgroundColor)
        binding.customEditTextPrice.setReadOnly(!editable, backgroundColor)
        binding.customEditTextPurchaseDate.setReadOnly(!editable, backgroundColor)
        binding.customEditTextPurchaseLocation.setReadOnly(!editable, backgroundColor)
        binding.customEditTextLoaned.setReadOnly(!editable, backgroundColor)
        binding.customEditTextVideoUrl.setReadOnly(!editable, backgroundColor)
        binding.customEditTextObservations.setReadOnly(!editable, backgroundColor)

        binding.editable = editable
    }

    fun getGameData(): GameResponse? {

        val pegi = resources.getStringArray(R.array.pegis)
            .firstOrNull { it == binding.spinnerPegis.selectedItem.toString() }
        val releaseDate = binding.customEditTextReleaseDate.getText().toDate(
            viewModel.dateFormatToShow,
            viewModel.language
        )
        val format =
            viewModel.formats.firstOrNull { it.name == binding.spinnerFormats.selectedItem.toString() }?.id
        val genre =
            viewModel.genres.firstOrNull { it.name == binding.spinnerGenres.selectedItem.toString() }?.id
        val state =
            when {
                binding.buttonPending.root.isSelected -> State.PENDING_STATE
                binding.buttonInProgress.root.isSelected -> State.IN_PROGRESS_STATE
                binding.buttonFinished.root.isSelected -> State.FINISHED_STATE
                else -> null
            }
        val purchaseDate = binding.customEditTextPurchaseDate.getText().toDate(
            viewModel.dateFormatToShow,
            viewModel.language
        )
        val price = try {
            binding.customEditTextPrice.getText().toDouble()
        } catch (e: NumberFormatException) {
            0.0
        }

        return viewModel.getGameData(
            pegi,
            binding.customEditTextDistributor.getText(),
            binding.customEditTextDeveloper.getText(),
            binding.customEditTextPlayers.getText(),
            releaseDate,
            binding.radioButtonYes.isChecked,
            format,
            genre,
            state,
            purchaseDate,
            binding.customEditTextPurchaseLocation.getText(),
            price,
            binding.customEditTextVideoUrl.getText(),
            binding.customEditTextLoaned.getText(),
            binding.customEditTextObservations.getText()
        )
    }

    fun buttonClicked(it: View) {

        binding.buttonPending.root.isSelected =
            if (it == binding.buttonPending.root) !it.isSelected else false
        binding.buttonInProgress.root.isSelected =
            if (it == binding.buttonInProgress.root) !it.isSelected else false
        binding.buttonFinished.root.isSelected =
            if (it == binding.buttonFinished.root) !it.isSelected else false
    }

    fun showMap() {

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
    //endregion

    //region Private methods
    private fun initializeUI() {

        val application = activity?.application
        viewModel = ViewModelProvider(
            this,
            GameDataViewModelFactory(application, game)
        )[GameDataViewModel::class.java]
        setupBindings()

        formatValues = ArrayList()
        formatValues.run {
            this.add(resources.getString((R.string.game_detail_select_format)))
            this.addAll(viewModel.formats.map { it.name })
        }
        genreValues = ArrayList()
        genreValues.run {
            this.add(resources.getString((R.string.game_detail_select_genre)))
            this.addAll(viewModel.genres.map { it.name })
        }
        val pegis = ArrayList<String>()
        pegis.run {
            this.add(resources.getString(R.string.game_detail_select_pegi))
            this.addAll(resources.getStringArray(R.array.pegis).toList())
        }

        with(binding) {

            spinnerFormats.adapter = Constants.getAdapter(requireContext(), formatValues)
            spinnerGenres.adapter = Constants.getAdapter(requireContext(), genreValues)
            spinnerPegis.apply {
                backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorPrimary
                    )
                )
                adapter = Constants.getAdapter(requireContext(), pegis)
            }

            customEditTextSaga.setReadOnly(true, 0)

            customEditTextDistributor.edit_text.setOnEditorActionListener { _, _, _ ->
                customEditTextDeveloper.requestFocus()
                true
            }
            customEditTextDeveloper.edit_text.setOnEditorActionListener { _, _, _ ->
                customEditTextPlayers.requestFocus()
                true
            }
            customEditTextPlayers.edit_text.setOnEditorActionListener { _, _, _ ->
                customEditTextPrice.requestFocus()
                true
            }
            customEditTextPrice.edit_text.setOnEditorActionListener { _, _, _ ->
                customEditTextLoaned.requestFocus()
                true
            }
            customEditTextLoaned.edit_text.setOnEditorActionListener { _, _, _ ->
                customEditTextVideoUrl.requestFocus()
                true
            }
            customEditTextVideoUrl.edit_text.setOnEditorActionListener { _, _, _ ->
                customEditTextObservations.requestFocus()
                true
            }

            fragment = this@GameDataFragment
        }

        showData(game)
        setEdition(enabled)
    }

    private fun setupBindings() {

        viewModel.gameDataLoading.observe(viewLifecycleOwner) { isLoading ->

            if (isLoading) {
                showLoading()
            } else {
                hideLoading()
            }
        }

        viewModel.gameDataError.observe(viewLifecycleOwner) { error ->

            if (error == null) {
                activity?.finish()
            } else {
                manageError(error)
            }
        }
    }

    private fun getText(value: String?): String {

        val newValue = value ?: Constants.EMPTY_VALUE
        return newValue.ifBlank { Constants.NO_VALUE }
    }
    //endregion
}
